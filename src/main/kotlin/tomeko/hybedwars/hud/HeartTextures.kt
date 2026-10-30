package tomeko.hybedwars.hud

//? if !forge {
import net.minecraft.client.Minecraft
//? if ornithe {
//import net.minecraft.util.ResourceLocation
//?} else {
import net.minecraft.resources.Identifier
//?}
import org.polyfrost.compose.render.FilterQuality
import org.polyfrost.compose.render.PolyBitmap
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.compose.render.RenderContext
import tomeko.hybedwars.utils.Debug

/**
 * Vanilla heart textures as bitmaps, for drawing hearts inside Compose HUDs.
 * Read from the game's resources (so resource packs apply) the first time they're needed.
 */
object HeartTextures {
    private class Sprite(val image: PolyBitmap, val u0: Float = 0f, val v0: Float = 0f, val u1: Float = 1f, val v1: Float = 1f)

    private var loaded = false
    private var container: Sprite? = null
    private var full: Sprite? = null

    /** Draws an empty heart outline, filled in red when [filled]. */
    fun draw(ctx: RenderContext, x: Float, y: Float, w: Float, h: Float, filled: Boolean) {
        load()
        container?.let { ctx.draw(it, x, y, w, h) }
        if (filled) full?.let { ctx.draw(it, x, y, w, h) }
    }

    private fun RenderContext.draw(sprite: Sprite, x: Float, y: Float, w: Float, h: Float) =
        image(sprite.image, x, y, w, h, sprite.u0, sprite.v0, sprite.u1, sprite.v1, PolyColor.WHITE, FilterQuality.None)

    private fun load() {
        if (loaded) return
        loaded = true

        try {
            //? if ornithe {
            /*// 1.8.9 keeps every HUD icon in one 256x256 sheet, scaled up by HD resource packs
            val sheet = decode("textures/gui/icons.png")
            container = Sprite(sheet, 16 / 256f, 0f, 25 / 256f, 9 / 256f)
            full = Sprite(sheet, 52 / 256f, 0f, 61 / 256f, 9 / 256f)
            *///?} else {
            container = Sprite(decode("textures/gui/sprites/hud/heart/container.png"))
            full = Sprite(decode("textures/gui/sprites/hud/heart/full.png"))
            //?}
        } catch (e: Exception) {
            Debug.forceLog("Couldn't load heart textures: $e")
        }
    }

    private fun decode(path: String): PolyBitmap =
        PolyBitmap.decode(read(path)) ?: throw IllegalStateException("can't decode $path")

    private fun read(path: String): ByteArray {
        //? if ornithe {
        //return Minecraft.getMinecraft().resourceManager.getResource(ResourceLocation(path)).inputStream.use { it.readBytes() }
        //?} else {
        return Minecraft.getInstance().resourceManager
            .getResource(Identifier.withDefaultNamespace(path))
            .orElseThrow { IllegalStateException("missing $path") }
            .open().use { it.readBytes() }
        //?}
    }
}
//?}
