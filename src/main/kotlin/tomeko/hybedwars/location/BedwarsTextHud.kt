package tomeko.hybedwars.location

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.hud.SingleTextHud
*///?} else {
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.align
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.hud.v1.Font
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.TextHud
//?}
import tomeko.hybedwars.utils.Constants

/**
 * Single line "Label: value" HUD used by the height limit, map and mode HUDs.
 * Hidden outside BedWars and whenever [value] has nothing to show.
 * On Fabric and Ornithe this is OneConfig's own TextHud, so it looks and sizes like
 * OneConfig's other text HUDs and gets a live preview card in the HUD editor.
 */
abstract class BedwarsTextHud(
    id: String,
    hudTitle: String,
    defaultLabel: String,
    hudDescription: String
)
//? if forge {
//: SingleTextHud(defaultLabel, false)
//?} else {
    : TextHud("${Constants.MOD_ID}_$id.json", hudTitle, Category.INFO, "$defaultLabel: ")
//?}
{
    //? if forge {
    //@Exclude
    //?}
    companion object {
        //? if !forge {
        const val CATEGORY_GENERAL = "General"
        const val SUBCATEGORY_OPTIONS = "Options"
        //?}

        // Same colors as Minecraft's §a, §e and §c
        //? if forge
        //@Exclude
        const val COLOR_GOOD = 0xFF55FF55.toInt()
        //? if forge
        //@Exclude
        const val COLOR_WARN = 0xFFFFFF55.toInt()
        //? if forge
        //@Exclude
        const val COLOR_BAD = 0xFFFF5555.toInt()

        private fun colorCode(color: Int?): String = when (color) {
            COLOR_GOOD -> "§a"
            COLOR_WARN -> "§e"
            COLOR_BAD -> "§c"
            else -> ""
        }
    }

    /**
     * Text after the label. When [example] is true (HUD editor, preview card) return a sample if there's no real data.
     * Return null to hide the HUD.
     */
    protected abstract fun value(example: Boolean): String?

    /** ARGB color for the value, or null to use the normal text color. */
    protected open fun valueColor(example: Boolean): Int? = null

    //? if forge {
    /*override fun getText(example: Boolean): String {
        val text = value(example) ?: return ""
        return colorCode(valueColor(example)) + text
    }

    override fun shouldShow(): Boolean =
        super.shouldShow() && BedwarsInfo.active && value(false) != null
    *///?} else {
    // False hides the whole HUD, background included
    private var visibleState: MutableState<Boolean> = mutableStateOf(true)

    /** The line split around the value, so the value can have its own color. */
    private class ColoredLine(val before: String, val value: String, val after: String, val valueColor: Int)

    // Null when the value uses the normal text color, then TextHud draws the line itself
    private var coloredState: MutableState<ColoredLine?> = mutableStateOf(null)

    override val description: String = hudDescription

    // The preview card uses an instance that isn't "real", so it always gets sample values
    private val example: Boolean
        get() = !isReal || HudManager.isEditing

    override fun getText(): String? {
        val example = example
        if (!example && !BedwarsInfo.active) return null
        return value(example)
    }

    override fun update(): Boolean {
        val example = example
        val value = if (example || BedwarsInfo.active) value(example) else null
        visibleState.value = value != null

        // OneConfig's text renderer ignores § color codes, so a colored value is drawn as its own piece
        val color = if (value != null) valueColor(example) else null
        coloredState.value = if (value == null || color == null) null else ColoredLine(
            before = (if (brackets) "[" else "") + prefix,
            value = value,
            after = suffix + (if (brackets) "]" else ""),
            valueColor = color
        )

        return super.update()
    }

    // The height limit changes as you move, 20 times a second keeps it in step
    override fun updateFrequency(): Long = 50_000_000L

    override fun hasBackground(): Boolean = visibleState.value

    @Composable
    override fun Content() {
        if (!visibleState.value) return

        val line = coloredState.value
        // Poppins is drawn by TextHud as one piece of text, so it keeps the normal color
        if (line == null || font != Font.Minecraft) {
            super.Content()
            return
        }

        // Same box as TextHud draws, with the line split into three pieces
        val padInsets = PolyInsets(padLeft, padTop, padRight, padBottom)
        val isStatic = staticWidth && staticW > 0f && staticH > 0f
        val boxModifier =
            if (isStatic) hudBackground().size(staticW, staticH).padding(padInsets)
            else hudBackground().padding(padInsets)

        val baseColor = PolyColor(textColor, textChroma, textChromaSpeed)
        val valueColor = PolyColor(line.valueColor)

        PolyBox(modifier = boxModifier) {
            PolyRow(modifier = if (isStatic) PolyModifier.align(alignment) else PolyModifier) {
                if (line.before.isNotEmpty()) LinePiece(line.before, baseColor)
                LinePiece(line.value, valueColor)
                if (line.after.isNotEmpty()) LinePiece(line.after, baseColor)
            }
        }
    }

    @Composable
    private fun LinePiece(text: String, color: PolyColor) {
        val cased = when (caseType) {
            1 -> text.uppercase()
            2 -> text.lowercase()
            else -> text
        }
        val formatted = buildString {
            if (textBold) append("§l")
            if (textItalic) append("§o")
            if (textUnderline) append("§n")
            append(cased)
        }
        PolyMcText(text = formatted, color = color, shadow = showShadow, scale = textScale)
    }

    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true

    override fun clone(): Hud = (super.clone() as BedwarsTextHud).also {
        it.visibleState = mutableStateOf(visibleState.value)
        it.coloredState = mutableStateOf(coloredState.value)
    }
    //?}
}
