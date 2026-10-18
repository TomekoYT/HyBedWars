package tomeko.hybedwars.coloredbeds

//? if 1.8.9 {
/*import net.minecraft.block.BlockBed
import net.minecraft.block.BlockDirectional
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.BlockPartRotation
import net.minecraft.client.renderer.block.model.BreakingFour
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.client.resources.model.ModelRotation
import net.minecraft.client.resources.model.SimpleBakedModel
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import org.lwjgl.util.vector.Vector3f
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.Debug
import java.net.URI
import kotlin.math.PI
import kotlin.math.atan2

object ColoredBeds {
    private const val URL = "https://data-v2.polyfrost.org/hytils/bed_locations.json"

    private val COLORS = hashMapOf(
        "yellow" to 0,
        "aqua" to 1,
        "white" to 2,
        "pink" to 3,
        "gray" to 4,
        "red" to 5,
        "blue" to 6,
        "green" to 7
    )

    @JvmField
    val COLORS_REVERSE = hashMapOf(
        0 to "yellow",
        1 to "aqua",
        2 to "white",
        3 to "pink",
        4 to "gray",
        5 to "red",
        6 to "blue",
        7 to "green"
    )

    private var defaultBedLocations = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
    private var setDefault = false

    @Volatile
    private var bedLocations: IntArray? = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)

    @Volatile
    private var locations: JsonObject? = null
    private var lastServer: String? = null

    fun register() {
        Thread {
            try {
                val body = URI.create(URL).toURL().readText()
                locations = JsonParser().parse(body).asJsonObject
            } catch (e: Exception) {
                Debug.log("Failed to load bed locations")
                e.printStackTrace()
            }
        }.start()

        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java) { onLocation() }
    }

    private fun processColors(array: JsonArray): IntArray {
        val colors = IntArray(8)
        var i = -1
        for (element in array) {
            i++
            val value = element.asJsonPrimitive
            colors[i] = if (value.isString) COLORS.getOrDefault(value.asString, 5) else value.asInt
        }
        return colors
    }

    @JvmStatic
    fun getBedLocations(): IntArray? {
        val locations = locations
        if (locations == null || HypixelPackets.currentServerType == null || !HypixelPackets.inGame || !HypixelPackets.inBedwars || HypixelPackets.currentMode == "BEDWARS_PRACTICE") {
            bedLocations = null
            return null
        }
        if (!setDefault) {
            setDefault = true
            COLORS_REVERSE.clear()
            var i = -1
            for (entry in locations.getAsJsonArray("default")) {
                i++
                COLORS[entry.asString] = i
                COLORS_REVERSE[i] = entry.asString
            }
            defaultBedLocations = COLORS_REVERSE.keys.toIntArray()
        }
        bedLocations?.let { return it }
        val mapName = HypixelPackets.currentMapName
        val mode = HypixelPackets.currentMode
        for (override in locations.getAsJsonArray("overrides")) {
            val overrideObject = override.asJsonObject
            if (overrideObject.has("maps") && mapName != null) {
                for (map in overrideObject.getAsJsonArray("maps")) {
                    if (map.asString.equals(mapName, ignoreCase = true)) {
                        return processColors(overrideObject.getAsJsonArray("locations")).also { bedLocations = it }
                    }
                }
            }
            if (overrideObject.has("modes") && mode != null) {
                for (m in overrideObject.getAsJsonArray("modes")) {
                    if (m.asString.equals(mode, ignoreCase = true)) {
                        return processColors(overrideObject.getAsJsonArray("locations")).also { bedLocations = it }
                    }
                }
            }
        }
        return defaultBedLocations.also { bedLocations = it }
    }

    private fun onLocation() {
        val serverId = HypixelPackets.currentServerName
        if (lastServer == serverId) return
        lastServer = serverId

        if (!HypixelPackets.inGame || !HypixelPackets.inBedwars) return

        bedLocations = null
        if (getBedLocations() != null) reloadRenderers()
    }

    fun reloadRenderers() {
        val mc = Minecraft.getMinecraft()
        mc.addScheduledTask(Runnable { mc.renderGlobal.loadRenderers() })
    }

    private fun getSide(x: Int, z: Int): Int = ((atan2(z.toDouble(), x.toDouble()) + PI * 4) / Math.toRadians(45.0)).toInt() % 8

    private const val OAK = "minecraft:blocks/planks_oak"
    const val COLORED_BED = "hybedwars:beds/"
    private val faceBakery = FaceBakery()
    private val states: HashMap<IBlockState, Array<IBakedModel?>> by lazy {
        HashMap<IBlockState, Array<IBakedModel?>>().apply {
            for (state in Blocks.bed.blockState.validStates) put(state, arrayOfNulls(8))
        }
    }

    @JvmStatic
    fun getBedModel(state: IBlockState?, pos: BlockPos?, model: IBakedModel?): IBakedModel? {
        if (!HyBedWarsConfig.coloredBeds) return null
        if (pos == null || state == null) return null
        if (state.block != Blocks.bed) return null
        val models = states[state] ?: return null
        val locations = getBedLocations() ?: return null
        val side = locations[getSide(pos.x, pos.z)]
        models[side]?.let { return it }
        if (model == null) return null
        val quads = ArrayList<BakedQuad>()
        val faceQuads: List<MutableList<BakedQuad>> = EnumFacing.values().map { ArrayList<BakedQuad>() }
        val facing = state.getValue(BlockDirectional.FACING)
        val bedPart = state.getValue(BlockBed.PART)
        val bedTexture = COLORED_BED + COLORS_REVERSE[side]
        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val particles = textureMap.getAtlasSprite(bedTexture)
        for (quad in model.getFaceQuads(facing)) {
            faceQuads[quad.face.ordinal].add(BreakingFour(quad, particles))
        }
        val rotation = ModelRotation.getModelRotation(0, facing.horizontalIndex * 90 - 180)
        val footPadding = if (bedPart == BlockBed.EnumPartType.FOOT) 16f else 0f
        val m = 0.25f
        fun quad(from: Vector3f, to: Vector3f, face: EnumFacing, texture: String, uv: FloatArray, uvRotation: Int, sprite: TextureAtlasSprite) =
            faceBakery.makeBakedQuad(from, to, BlockPartFace(face, -1, texture, BlockFaceUV(uv, uvRotation)), sprite, face, rotation, BlockPartRotation(Vector3f(8f, 0f, 8f), EnumFacing.Axis.Y, 0f, false), false, true)
        quads.add(quad(Vector3f(0f, 3f, 0f), Vector3f(16f, 3f, 16f), EnumFacing.DOWN, OAK, floatArrayOf(0f, 0f, 16f, 16f), 0, textureMap.getAtlasSprite(OAK)))
        if (bedPart != BlockBed.EnumPartType.FOOT) {
            quads.add(quad(Vector3f(0f, 0f, 0f), Vector3f(16f, 9f, 0f), EnumFacing.NORTH, bedTexture, floatArrayOf(25f * m, 9f * m, 9f * m, 0f), 0, particles))
        } else {
            quads.add(quad(Vector3f(0f, 0f, 16f), Vector3f(16f, 9f, 16f), EnumFacing.SOUTH, bedTexture, floatArrayOf(25f * m, 50f * m, 9f * m, 41f * m), 180, particles))
        }
        quads.add(quad(Vector3f(0f, 9f, 0f), Vector3f(16f, 9f, 16f), EnumFacing.UP, bedTexture, floatArrayOf(9f * m, 9f * m + footPadding * m, 25f * m, 25f * m + footPadding * m), 0, particles))
        quads.add(quad(Vector3f(0f, 0f, 0f), Vector3f(0f, 9f, 16f), EnumFacing.WEST, bedTexture, floatArrayOf(0f, 9f * m + footPadding * m, 9f * m, 25f * m + footPadding * m), 270, particles))
        quads.add(quad(Vector3f(16f, 0f, 0f), Vector3f(16f, 9f, 16f), EnumFacing.EAST, bedTexture, floatArrayOf(25f * m, 9f * m + footPadding * m, 34f * m, 25f * m + footPadding * m), 90, particles))
        return SimpleBakedModel(quads, faceQuads, true, false, particles, model.itemCameraTransforms).also { models[side] = it }
    }

    @JvmStatic
    fun reloadModels() {
        for (models in states.values) models.fill(null)
    }
}
*///?}
