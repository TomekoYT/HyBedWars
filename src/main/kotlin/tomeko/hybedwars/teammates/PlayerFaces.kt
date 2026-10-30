package tomeko.hybedwars.teammates

//? if !forge {
import net.minecraft.client.Minecraft
//? if ornithe {
/*import net.minecraft.client.resources.DefaultPlayerSkin
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import tomeko.hybedwars.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.resources.DefaultPlayerSkin
//?}
import org.polyfrost.compose.render.PolyBitmap
import tomeko.hybedwars.utils.Debug

/**
 * Player faces (head plus hat layer) as 8x8 bitmaps, for drawing heads in Compose HUDs.
 *
 * Compose HUDs can't draw Minecraft textures directly, so each skin's pixels are read once on the
 * client tick (where touching textures is safe) and cached. Until a face is ready, [get] returns null.
 */
object PlayerFaces {
    private const val FACE = 8
    private const val MAX_CACHED = 200
    private const val MAX_READS_PER_TICK = 8
    private const val RETRY_TICKS = 40

    private val faces = HashMap<String, PolyBitmap>()
    private val pending = LinkedHashMap<String, SkinId>()
    private val failedAt = HashMap<String, Int>()
    private var ticks = 0

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { readPending() }
    }

    /** The face for [skin] (null means the default skin), or null while it hasn't been read yet. */
    @Synchronized
    fun get(skin: SkinId?): PolyBitmap? {
        val id = skin ?: defaultSkin()
        val key = id.toString()
        faces[key]?.let { return it }

        val failed = failedAt[key]
        if (failed == null || ticks - failed >= RETRY_TICKS) pending[key] = id
        return null
    }

    @Synchronized
    private fun readPending() {
        ticks++
        if (pending.isEmpty()) return
        if (faces.size > MAX_CACHED) faces.clear()

        val batch = pending.entries.take(MAX_READS_PER_TICK)
        for ((key, id) in batch) {
            pending.remove(key)
            val face = try {
                readFace(id)
            } catch (e: Exception) {
                Debug.log("Couldn't read skin $id: $e")
                null
            }

            if (face != null) {
                faces[key] = face
                failedAt.remove(key)
            } else {
                // Skins still downloading can't be read yet, try again in a bit
                failedAt[key] = ticks
            }
        }
    }

    private fun defaultSkin(): SkinId =
        //? if ornithe {
        //DefaultPlayerSkin.getDefaultSkinLegacy()
        //?} else {
        DefaultPlayerSkin.getDefaultTexture()
        //?}

    /** Reads the face and hat layer of a skin texture into an 8x8 bitmap. */
    private fun readFace(id: SkinId): PolyBitmap? {
        val skin = readSkin(id) ?: return null
        val (width, pixelAt) = skin
        if (width < 64) return null

        val step = width / 64
        val face = IntArray(FACE * FACE)
        for (y in 0 until FACE) {
            for (x in 0 until FACE) {
                val head = pixelAt((8 + x) * step, (8 + y) * step)
                val hat = pixelAt((40 + x) * step, (8 + y) * step)
                face[y * FACE + x] = over(hat, head or (0xFF shl 24))
            }
        }
        return toBitmap(face)
    }

    /** Skin width and a way to read ARGB pixels from it, or null if it isn't readable yet. */
    private fun readSkin(id: SkinId): Pair<Int, (Int, Int) -> Int>? {
        val mc =
            //? if ornithe {
            //Minecraft.getMinecraft()
            //?} else {
            Minecraft.getInstance()
            //?}

        //? if ornithe {
        /*// 1.8.9 doesn't keep skin pixels around, so read them back from the uploaded texture
        val texture = mc.textureManager.getTexture(id) ?: run {
            mc.textureManager.bindTexture(id)
            mc.textureManager.getTexture(id)
        } ?: return null
        val glId = texture.glTextureId

        val previousUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        val previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
        try {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glId)
            val width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
            val height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
            if (width <= 0 || height < width / 2) return null

            val buffer = BufferUtils.createByteBuffer(width * height * 4)
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)

            return width to { x, y ->
                val i = (y * width + x) * 4
                val r = buffer.get(i).toInt() and 0xFF
                val g = buffer.get(i + 1).toInt() and 0xFF
                val b = buffer.get(i + 2).toInt() and 0xFF
                val a = buffer.get(i + 3).toInt() and 0xFF
                (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        } finally {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture)
            GL13.glActiveTexture(previousUnit)
        }
        *///?} else {
        // Downloaded skins keep their pixels in memory
        val pixels = (mc.textureManager.getTexture(id) as? DynamicTexture)?.pixels
        if (pixels != null) return pixels.width to { x, y -> pixels.getPixel(x, y) }

        // Default skins are plain resources
        val bytes = mc.resourceManager.getResource(id).orElse(null)?.open()?.use { it.readBytes() } ?: return null
        val bitmap = PolyBitmap.decode(bytes) ?: return null
        val rgba = bitmap.pixels
        return bitmap.width to { x, y ->
            val i = (y * bitmap.width + x) * 4
            val r = rgba[i].toInt() and 0xFF
            val g = rgba[i + 1].toInt() and 0xFF
            val b = rgba[i + 2].toInt() and 0xFF
            val a = rgba[i + 3].toInt() and 0xFF
            (a shl 24) or (r shl 16) or (g shl 8) or b
        }
        //?}
    }

    /** [top] drawn over the opaque [bottom], both ARGB. */
    private fun over(top: Int, bottom: Int): Int {
        val a = top ushr 24
        if (a == 0) return bottom
        if (a == 0xFF) return top

        fun mix(shift: Int): Int {
            val t = (top shr shift) and 0xFF
            val b = (bottom shr shift) and 0xFF
            return (t * a + b * (0xFF - a)) / 0xFF
        }
        return (0xFF shl 24) or (mix(16) shl 16) or (mix(8) shl 8) or mix(0)
    }

    private fun toBitmap(argb: IntArray): PolyBitmap {
        val bytes = ByteArray(argb.size * 4)
        for ((i, c) in argb.withIndex()) {
            bytes[i * 4] = (c shr 16).toByte()
            bytes[i * 4 + 1] = (c shr 8).toByte()
            bytes[i * 4 + 2] = c.toByte()
            bytes[i * 4 + 3] = (c ushr 24).toByte()
        }
        return PolyBitmap(FACE, FACE, bytes)
    }
}
//?}
