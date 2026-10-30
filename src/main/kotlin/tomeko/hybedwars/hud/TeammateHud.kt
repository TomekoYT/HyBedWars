package tomeko.hybedwars.hud

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.DefaultPlayerSkin
*///?} else {
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyCanvas
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.FilterQuality
import org.polyfrost.compose.render.PolyBitmap
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import tomeko.hybedwars.teammates.PlayerFaces
import tomeko.hybedwars.teammates.SkinId
//?}
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.utils.HypixelPackets
import tomeko.hybedwars.teammates.Teammates
import tomeko.hybedwars.teammates.Teammates.Teammate
import tomeko.hybedwars.utils.Constants
import tomeko.hybedwars.utils.removeFormatting
import java.util.Locale
import kotlin.math.ceil

/**
 * Lists your teammates and their health.
 * On Fabric and Ornithe this is a Compose HUD, so the HUD editor shows a live preview card for it.
 */
class TeammateHud
//? if forge {
//: BasicHud(true)
//?} else {
    : Hud("${Constants.MOD_ID}_teammates.json", "BedWars Teammates", Category.COMBAT)
//?}
{
    //? if forge {
    //@Exclude
    //?}
    companion object {
        //? if !forge {
        fun register() {
            HudManager.register(TeammateHud(), Constants.MOD_ID, Constants.MOD_ICON)
            PlayerFaces.register()
        }

        //? if forge
        //@Exclude
        private const val SUBCATEGORY_HEALTH = "Health"
        //? if forge
        //@Exclude
        private const val SUBCATEGORY_PLAYERS = "Players"

        //? if forge
        //@Exclude
        private const val COLUMN_GAP = 4f
        //? if forge
        //@Exclude
        private const val ROW_GAP = 1f

        // Vanilla colors of the § codes 0-9 and a-f
        //? if forge
        //@Exclude
        private const val LEGACY_CODES = "0123456789abcdef"
        //? if forge
        //@Exclude
        private val LEGACY_RGB = intArrayOf(
            0xFF000000.toInt(), 0xFF0000AA.toInt(), 0xFF00AA00.toInt(), 0xFF00AAAA.toInt(),
            0xFFAA0000.toInt(), 0xFFAA00AA.toInt(), 0xFFFFAA00.toInt(), 0xFFAAAAAA.toInt(),
            0xFF555555.toInt(), 0xFF5555FF.toInt(), 0xFF55FF55.toInt(), 0xFF55FFFF.toInt(),
            0xFFFF5555.toInt(), 0xFFFF55FF.toInt(), 0xFFFFFF55.toInt(), 0xFFFFFFFF.toInt()
        )
        //? if forge
        //@Exclude
        private val GRAY = LEGACY_RGB[7]
        //? if forge
        //@Exclude
        private val WHITE = LEGACY_RGB[15]

        //? if forge
        //@Exclude
        private val DEAD_HEAD = PolyColor(0x80FFFFFF.toInt())
        //?}

        //? if forge
        //@Exclude
        private const val HEART_SIZE = 9
        //? if forge
        //@Exclude
        private const val HEAD_SIZE = 8

        // Placeholder rows for the HUD editor and its preview card
        //? if forge
        //@Exclude
        private val EXAMPLE = listOf(
            Teammate("§cTeammateOne", 20f, 20f),
            Teammate("§cTeammateTwo", 11f, 20f),
            Teammate("§cTeammateThree", 4f, 20f),
            Teammate("§cTeammateFour", 0f, 20f, true)
        )
    }

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Show Player Head",
        description = "Show each teammate's skin face next to their name.",
        //? if !forge {
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var showHead = true

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Show Heart Icon",
        //? if !forge {
        subcategory = SUBCATEGORY_HEALTH
        //?}
    )
    var showHeart = true

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Color Health",
        description = "Green when healthy, yellow when hurt, red when low.",
        //? if !forge {
        subcategory = SUBCATEGORY_HEALTH
        //?}
    )
    var colorHealth = true

    //? if forge {
    /*@Switch(name = "Text Shadow")
    var textShadow = true
    *///?}

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Show Yourself",
        //? if !forge {
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var showSelf = false

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Show Dead Teammates",
        description = "Keep listing teammates while they respawn or after they've been final killed.",
        //? if !forge {
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var showDead = true

    @Slider(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Max Players Shown",
        description = "The most teammates the HUD will list at once.",
        min = 1f, max = 40f,
        //? if forge {
        //step = 1
        //?} else {
        step = 1f,
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var maxPlayers = 10f

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Nearby Teammates in Castle",
        description = "In Castle (40v40), only list the closest teammates within range instead of the whole team.",
        //? if !forge {
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var castleNearby = true

    @Slider(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Castle Range",
        description = "How close (in blocks) a teammate must be to show up in Castle.",
        min = 5f, max = 100f,
        //? if forge {
        //step = 1
        //?} else {
        step = 1f,
        subcategory = SUBCATEGORY_PLAYERS
        //?}
    )
    var castleRange = 25f

    /** Only in BedWars games: in lobbies everyone with your rank shows up as a "teammate". */
    private val active: Boolean
        get() = HyBedWarsConfig.debugModeEnabled || (HypixelPackets.inBedwars && HypixelPackets.inGame)

    private fun teammates(example: Boolean): List<Teammate> {
        if (example) return EXAMPLE
        if (!active) return emptyList()

        val castle = HypixelPackets.currentMode?.uppercase(Locale.ROOT)?.contains("CASTLE") == true
        val range = if (castleNearby && castle) castleRange else 0f
        return Teammates.collect(showSelf, showDead, range, maxPlayers.toInt())
    }

    private fun nameText(t: Teammate): String =
        if (t.dead) "§7§m" + t.displayName.removeFormatting() else t.displayName

    private fun healthText(t: Teammate): String {
        if (t.dead) return "§7Dead"
        if (t.health < 0) return "§7?"

        var color = "§f"
        if (colorHealth) {
            val fraction = t.health / t.maxHealth.coerceAtLeast(1f)
            color = when {
                fraction > 0.6f -> "§a"
                fraction > 0.3f -> "§e"
                else -> "§c"
            }
        }
        return color + ceil(t.health).toInt()
    }

    //? if !forge {
    /**
     * OneConfig's Compose text renderer skips § color codes instead of applying them,
     * so every piece of text carries its color separately. Null means the HUD's own text color.
     */
    private class Row(
        val name: String,
        val nameColor: Int?,
        val health: String,
        val healthColor: Int?,
        val alive: Boolean,
        val skin: SkinId?
    )

    // Starts with the example rows so the HUD editor's preview card has something to show
    private var rowsState: MutableState<List<Row>> = mutableStateOf(EXAMPLE.map(::toRow))

    private fun toRow(t: Teammate): Row {
        val health = healthText(t)
        return Row(
            name = t.displayName.removeFormatting(),
            nameColor = if (t.dead) GRAY else legacyColor(t.displayName),
            health = health.removeFormatting(),
            // §f means "no health color", so it follows the HUD's text color
            healthColor = legacyColor(health).takeUnless { it == WHITE },
            alive = !t.dead,
            skin = t.skin
        )
    }

    /** ARGB of the color code a string starts with, or null if it doesn't start with one. */
    private fun legacyColor(text: String): Int? {
        if (text.length < 2 || text[0] != '§') return null
        val index = LEGACY_CODES.indexOf(text[1].lowercaseChar())
        return if (index < 0) null else LEGACY_RGB[index]
    }

    init {
        padLeft = 4f
        padRight = 4f
        padTop = 4f
        padBottom = 4f
        staticWidth = false
    }

    override val description: String = "Your teammates and their health in BedWars."

    override fun update(): Boolean {
        rowsState.value = teammates(!isReal || HudManager.isEditing).map(::toRow)
        return true
    }

    // Collecting teammates walks the whole tab list, 20 times a second is plenty
    override fun updateFrequency(): Long = 50_000_000L

    override fun setup() {
        super.setup()
        update()
    }

    // No background box when there is nobody to list
    override fun hasBackground(): Boolean = rowsState.value.isNotEmpty()

    @Composable
    override fun Content() {
        val rows = rowsState.value
        if (rows.isEmpty()) return

        val scale = textScale
        val color = PolyColor(textColor, textChroma, textChromaSpeed)
        val shadow = showShadow

        // One column each for heads, names, hearts and health so every row lines up
        PolyBox(modifier = hudBackground().padding(PolyInsets(padLeft, padTop, padRight, padBottom))) {
            PolyRow(gap = COLUMN_GAP * scale) {
                if (showHead) {
                    PolyColumn(gap = ROW_GAP * scale) {
                        for (row in rows) Head(PlayerFaces.get(row.skin), row.alive, scale)
                    }
                }
                PolyColumn(gap = ROW_GAP * scale) {
                    for (row in rows) {
                        val nameColor = row.nameColor?.let(::PolyColor) ?: color
                        if (row.alive) PolyMcText(row.name, color = nameColor, shadow = shadow, scale = scale)
                        else StruckText(row.name, nameColor, shadow, scale)
                    }
                }
                if (showHeart) {
                    PolyColumn(gap = ROW_GAP * scale) {
                        for (row in rows) Heart(row.alive, HEART_SIZE * scale)
                    }
                }
                PolyColumn(gap = ROW_GAP * scale) {
                    for (row in rows) {
                        val healthColor = row.healthColor?.let(::PolyColor) ?: color
                        PolyMcText(row.health, color = healthColor, shadow = shadow, scale = scale)
                    }
                }
            }
        }
    }

    /** Minecraft text with a strikethrough, which OneConfig's text renderer doesn't support. */
    @Composable
    private fun StruckText(text: String, color: PolyColor, shadow: Boolean, scale: Float) {
        val width = McFontQueue.measureTextWidth(text, scale)
        val height = McFontQueue.measureTextHeight(scale)

        PolyCanvas(modifier = PolyModifier.size(width, height)) { x, y, _, _ ->
            val argb = color.argb
            mcText(text, x, y, argb, shadow, scale)

            // Same place as vanilla: a one pixel line through the middle, with its own shadow
            if (shadow) rect(x + scale, y + 4 * scale, width, scale, PolyColor(shadowOf(argb)))
            rect(x, y + 3 * scale, width, scale, PolyColor(argb))
        }
    }

    // Vanilla text shadows are the text color at a quarter brightness
    private fun shadowOf(argb: Int): Int =
        (argb and 0xFF000000.toInt()) or ((argb and 0xFCFCFC) shr 2)

    /** A player's face, faded out when they're dead. Keeps its space while the skin is still loading. */
    @Composable
    private fun Head(face: PolyBitmap?, alive: Boolean, scale: Float) {
        // As tall as a line of text so the head column lines up with the names
        val size = HEAD_SIZE * scale
        PolyCanvas(modifier = PolyModifier.size(size, McFontQueue.measureTextHeight(scale))) { x, y, _, _ ->
            if (face == null) return@PolyCanvas
            image(face, x, y, size, size, 0f, 0f, 1f, 1f, if (alive) PolyColor.WHITE else DEAD_HEAD, FilterQuality.None)
        }
    }

    @Composable
    private fun Heart(full: Boolean, size: Float) {
        PolyCanvas(modifier = PolyModifier.size(size, size)) { x, y, w, h ->
            HeartTextures.draw(this, x, y, w, h, full)
        }
    }

    override fun minimumSize(): Pair<Float, Float> =
        (padLeft + padRight + 2f) to (padTop + padBottom + 2f)

    override fun defaultPosition(): Pair<Float, Float> = 10f to 40f
    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true

    override fun clone(): Hud = (super.clone() as TeammateHud).also {
        it.rowsState = mutableStateOf(rowsState.value)
    }
    //?} else {
    /*@Exclude
    private var actualWidth = 1f

    @Exclude
    private var actualHeight = 10f

    override fun draw(matrices: UMatrixStack, x: Float, y: Float, scale: Float, example: Boolean) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0f)
        GlStateManager.scale(scale, scale, 1f)
        drawRows(teammates(example))
        GlStateManager.popMatrix()
    }

    override fun getWidth(scale: Float, example: Boolean): Float = actualWidth * scale
    override fun getHeight(scale: Float, example: Boolean): Float = actualHeight * scale

    override fun shouldShow(): Boolean = super.shouldShow() && active

    private fun drawRows(rows: List<Teammate>) {
        val mc = Minecraft.getMinecraft()
        val font = mc.fontRendererObj
        val rowHeight = 10
        val gap = 4

        // Health goes in its own column so every row lines up
        var nameWidth = 0
        var healthWidth = 0
        for (t in rows) {
            nameWidth = maxOf(nameWidth, font.getStringWidth(nameText(t)))
            healthWidth = maxOf(healthWidth, font.getStringWidth(healthText(t)))
        }

        val nameX = if (showHead) HEAD_SIZE + 2 else 0
        val heartX = nameX + nameWidth + gap
        val healthX = if (showHeart) heartX + HEART_SIZE + 1 else heartX

        var y = 0
        for (t in rows) {
            if (showHead) {
                // Face then hat layer, the same way the tab list draws heads
                mc.textureManager.bindTexture(t.skin ?: DefaultPlayerSkin.getDefaultSkinLegacy())
                GlStateManager.enableBlend()
                GlStateManager.color(1f, 1f, 1f, if (t.dead) 0.5f else 1f)
                Gui.drawScaledCustomSizeModalRect(0, y, 8f, 8f, 8, 8, HEAD_SIZE, HEAD_SIZE, 64f, 64f)
                Gui.drawScaledCustomSizeModalRect(0, y, 40f, 8f, 8, 8, HEAD_SIZE, HEAD_SIZE, 64f, 64f)
                GlStateManager.color(1f, 1f, 1f, 1f)
            }
            font.drawString(nameText(t), nameX.toFloat(), y.toFloat(), 0xFFFFFFFF.toInt(), textShadow)
            if (showHeart) {
                mc.textureManager.bindTexture(Gui.icons)
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.enableBlend()
                // Outline, then the red fill on top
                Gui.drawModalRectWithCustomSizedTexture(heartX, y - 1, 16f, 0f, HEART_SIZE, HEART_SIZE, 256f, 256f)
                if (!t.dead) Gui.drawModalRectWithCustomSizedTexture(heartX, y - 1, 52f, 0f, HEART_SIZE, HEART_SIZE, 256f, 256f)
            }
            font.drawString(healthText(t), healthX.toFloat(), y.toFloat(), 0xFFFFFFFF.toInt(), textShadow)
            y += rowHeight
        }

        actualWidth = (healthX + healthWidth).toFloat().coerceAtLeast(1f)
        actualHeight = (rows.size * rowHeight - 1).toFloat().coerceAtLeast(rowHeight.toFloat())
    }
    *///?}
}
