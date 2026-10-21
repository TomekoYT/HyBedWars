package tomeko.hybedwars.hud

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.hud.SingleTextHud
*///?} else {
import net.minecraft.client.Minecraft
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
//?}
//? if ornithe {
/*import net.minecraft.client.gui.Gui
*///?}
//? if fabric {
import net.minecraft.client.gui.GuiGraphicsExtractor
//?}
import tomeko.hybedwars.utils.Constants

/**
 * Single line "Label: value" HUD used by the height limit, map and mode HUDs.
 * Hidden outside BedWars and whenever [value] has nothing to show.
 */
abstract class BedwarsTextHud(
    id: String,
    hudTitle: String,
    defaultLabel: String
)
//? if forge {
/*: SingleTextHud(defaultLabel, false)
*///?} else {
    : LegacyHud("${Constants.MOD_ID}_$id.json", hudTitle, Hud.Category.INFO)
//?}
{
    //? if forge {
    //@Exclude
    //?}
    companion object {
        //? if !forge {
        const val CATEGORY_GENERAL = "General"
        const val SUBCATEGORY_TEXT = "Text"
        const val SUBCATEGORY_OPTIONS = "Options"
        private const val CATEGORY_BACKGROUND = "Background"
        //?}

        // Same colors as Minecraft's §a, §e and §c
        const val COLOR_GOOD = 0xFF55FF55.toInt()
        const val COLOR_WARN = 0xFFFFFF55.toInt()
        const val COLOR_BAD = 0xFFFF5555.toInt()
    }

    /**
     * Text after the label. When [example] is true (HUD editor) return a sample if there's no real data.
     * Return null to hide the HUD.
     */
    protected abstract fun value(example: Boolean): String?

    /** ARGB color for the value, or null to use the normal text color. */
    protected open fun valueColor(example: Boolean): Int? = null

    //? if forge {
    /*override fun getText(example: Boolean): String {
        val text = value(example) ?: return ""
        val code = when (valueColor(example)) {
            COLOR_GOOD -> "§a"
            COLOR_WARN -> "§e"
            COLOR_BAD -> "§c"
            else -> ""
        }
        return code + text
    }

    override fun shouldShow(): Boolean =
        super.shouldShow() && BedwarsInfo.active && value(false) != null
    *///?} else {
    @Text(
        title = "Label",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_TEXT
    )
    var label = defaultLabel

    @Switch(
        title = "Show Label",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_TEXT
    )
    var showLabel = true

    @Color(
        title = "Text Color",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_TEXT
    )
    var txtColor = PolyColor(0xFFFFFFFF.toInt())

    @Switch(
        title = "Text Shadow",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_TEXT
    )
    var textShadow = true

    @Color(
        title = "Text Shadow Color",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_TEXT
    )
    var textShadowColor = PolyColor(0xFF000000.toInt())

    @Switch(
        title = "Show Background",
        category = CATEGORY_BACKGROUND
    )
    var background = false

    @Color(
        title = "Background Color",
        category = CATEGORY_BACKGROUND
    )
    var backgroundColor = PolyColor(0x80000000.toInt())

    @Slider(
        title = "Background Padding",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_BACKGROUND
    )
    var backgroundPadding = 3f

    private var actualWidth = 1f
    private var actualHeight = 1f

    override fun render(
        //? if fabric {
        mcCtx: GuiGraphicsExtractor
        //?}
    ) {
        val editing = HudManager.isEditing
        if (!editing && !BedwarsInfo.active) return

        val value = value(editing) ?: return

        val mc =
            //? if 1.8.9 {
            /*Minecraft.getMinecraft()
            *///?} else {
            Minecraft.getInstance()
            //?}

        val labelText = if (showLabel && label.isNotBlank()) "$label: " else ""

        val labelWidth = textWidth(mc, labelText)
        val valueWidth = textWidth(mc, value)
        val fontHeight =
            //? if 1.8.9 {
            /*mc.fontRendererObj.FONT_HEIGHT
            *///?} else {
            mc.font.lineHeight
            //?}

        val pad = if (background) backgroundPadding.toInt() else 0
        val totalWidth = labelWidth + valueWidth + pad * 2
        val totalHeight = fontHeight + pad * 2

        if (background) {
            //? if ornithe {
            /*Gui.drawRect(
            *///?} else {
            mcCtx.fill(
            //?}
                0,
                0,
                totalWidth,
                totalHeight,
                backgroundColor.argb
            )
        }

        drawText(
            //? if fabric {
            mcCtx,
            //?}
            mc, labelText, pad, pad, txtColor.argb
        )
        drawText(
            //? if fabric {
            mcCtx,
            //?}
            mc, value, pad + labelWidth, pad, valueColor(editing) ?: txtColor.argb
        )

        actualWidth = totalWidth.toFloat()
        actualHeight = totalHeight.toFloat()
    }

    private fun textWidth(mc: Minecraft, text: String): Int {
        if (text.isEmpty()) return 0
        //? if 1.8.9 {
        /*return mc.fontRendererObj.getStringWidth(text)
        *///?} else {
        return mc.font.width(text)
        //?}
    }

    private fun drawText(
        //? if fabric {
        mcCtx: GuiGraphicsExtractor,
        //?}
        mc: Minecraft,
        text: String,
        x: Int,
        y: Int,
        color: Int
    ) {
        if (text.isEmpty()) return

        if (textShadow) {
            //? if ornithe {
            /*mc.fontRendererObj.drawString(text, (x + 1).toFloat(), (y + 1).toFloat(), textShadowColor.argb, false)
            *///?} else {
            mcCtx.text(mc.font, text, x + 1, y + 1, textShadowColor.argb, false)
            //?}
        }

        //? if ornithe {
        /*mc.fontRendererObj.drawString(text, x.toFloat(), y.toFloat(), color, false)
        *///?} else {
        mcCtx.text(mc.font, text, x, y, color, false)
        //?}
    }

    override val width: Float get() = actualWidth
    override val height: Float get() = actualHeight
    override fun minimumSize(): Pair<Float, Float> = actualWidth to actualHeight
    override fun defaultPosition(): Pair<Float, Float> = 10f to 10f
    override fun update(): Boolean = true
    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true
    //?}
}
