package tomeko.hybedwars.resources

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UGraphics
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
*///?} else {
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
//? if ornithe {
/*import net.minecraft.init.Items
import net.minecraft.item.Item
*///?} else {
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
//?}
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.absoluteAt
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.item.PolyItemIcon
//?}
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.Constants

class BedwarsResourceDisplay
//? if forge {
/*: BasicHud(false)
*///?} else {
    : Hud("${Constants.MOD_ID}_bedwars_resource_display.json", "BedWars Resource Display", Category.COMBAT)
//?}
{
    //? if forge
    //@Exclude
    companion object {
        //? if !forge {
        fun register() {
            HudManager.register(BedwarsResourceDisplay(), Constants.MOD_ID, Constants.MOD_ICON)
        }
        //?}

        private fun iron(): Item =
        //? if 1.8.9 {
        /*Items.iron_ingot
            *///?} else {
            Items.IRON_INGOT
        //?}

        private fun gold(): Item =
        //? if 1.8.9 {
        /*Items.gold_ingot
            *///?} else {
            Items.GOLD_INGOT
        //?}

        private fun diamond(): Item =
        //? if 1.8.9 {
        /*Items.diamond
            *///?} else {
            Items.DIAMOND
        //?}

        private fun emerald(): Item =
        //? if 1.8.9 {
        /*Items.emerald
            *///?} else {
            Items.EMERALD
        //?}

        private fun items(): List<Item> = listOf(iron(), gold(), diamond(), emerald())

        //? if !forge {
        private const val SUBCATEGORY_GENERAL = "General"
        private const val SUBCATEGORY_FORMATTING = "Formatting"

        private const val ICON_SIZE = 16f

        private fun iconId(item: Item): String =
            when (item) {
                iron() -> "minecraft:iron_ingot"
                gold() -> "minecraft:gold_ingot"
                diamond() -> "minecraft:diamond"
                else -> "minecraft:emerald"
            }
        //?}
    }

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Iron",
        description = "Show iron counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showIron = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Gold",
        description = "Show gold counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showGold = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Diamond",
        description = "Show diamond counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showDiamond = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Emerald",
        description = "Show emerald counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showEmerald = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Inventory",
        description = "Show inventory counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showInventory = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Ender Chest",
        description = "Show ender chest counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showEnderChest = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show Total",
        description = "Show (inventory + ender chest) counter",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var showTotal = true

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Mode",
        description = "Change display mode",
        options = ["Vertical", "Horizontal"],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var mode = 0

    @Slider(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Counters Padding",
        description = "Change spacing between the counters",
        min = 0f,
        max = 10f,
        //? if !forge {
        step = 0.1f,
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var countersPadding = 5f

    @Slider(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Icon Padding",
        description = "Change spacing between the icon and the text",
        min = 0f,
        max = 10f,
        //? if !forge {
        step = 0.1f,
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var iconPadding = 5f

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Total Side",
        description = "Change side of the total counter display",
        options = ["Left", "Right"],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var totalSide = 1

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Icon Side",
        description = "Change side of the icons",
        options = ["Left", "Right"],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var iconSide = 0

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Padding Side",
        description = "Change padding side of the display in vertical mode",
        options = ["Left", "Middle", "Right"],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var paddingSide = 0

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Compact Numbers",
        description = "Don't show unnecessary 0's when either inventory or ender chest counter equal to zero",
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var compactNumbers = true

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Hide When Zero",
        description = "Hide items with both inventory and ender chest counters equal to zero",
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var hideWhenZero = false

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Show In Lobby",
        description = "Show in Hypixel BedWars lobby",
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var showInLobby = true

    //? if forge {
    /*@Dropdown(
        name = "Text Shadow",
        description = "Change text shadow",
        options = ["No Shadow", "Shadow", "Full Shadow"]
    )
    var textType = 0

    @Color(
        name = "Text Color",
        description = "Change text color"
    )
    var textColor: OneColor = OneColor(255, 255, 255)
    *///?}

    private fun amounts(item: Item): Pair<Int, Int> =
        (ItemTracker.inventory[item] ?: 0) to (ItemTracker.enderChest[item] ?: 0)

    private fun showItem(item: Item, inventoryAmount: Int, enderChestAmount: Int): Boolean =
        (!hideWhenZero ||
                (showTotal && inventoryAmount + enderChestAmount > 0) ||
                (showInventory && inventoryAmount > 0) ||
                (showEnderChest && enderChestAmount > 0)) &&
                when (item) {
                    iron() -> showIron
                    gold() -> showGold
                    diamond() -> showDiamond
                    emerald() -> showEmerald
                    else -> false
                }

    private fun getText(item: Item, inventoryAmount: Int, enderChestAmount: Int): String {
        fun StringBuilder.appendAmount(amount: String) =
            apply {
                this.append(if (this.isNotEmpty()) " " else "")
                this.append(amount)
            }

        fun StringBuilder.appendTotal() =
            if (showInventory || showEnderChest) {
                this.appendAmount("(${inventoryAmount + enderChestAmount})")
            } else {
                append("${inventoryAmount + enderChestAmount}")
            }

        val totalOnly = !(showInventory || showEnderChest)

        val shouldShowFull = !compactNumbers || enderChestAmount != 0

        val text = StringBuilder()
        if ((shouldShowFull || totalOnly) && totalSide == 0 && showTotal) {
            text.appendTotal()
        }

        if (showInventory) {
            text.appendAmount(inventoryAmount.toString())

            if (showEnderChest && shouldShowFull) {
                text.append(" +")
            }
        }

        if (showEnderChest && (shouldShowFull || !showInventory)) {
            text.appendAmount(enderChestAmount.toString())
        }

        if ((shouldShowFull || totalOnly) && totalSide == 1 && showTotal) {
            text.appendTotal()
        }

        text.append(" ")

        return text.toString()
    }

    //? if forge {
    /*@Exclude
    private var actualWidth = 1f

    @Exclude
    private var actualHeight = 1f

    private fun showItem(item: Item): Boolean {
        val (inventoryAmount, enderChestAmount) = amounts(item)
        return showItem(item, inventoryAmount, enderChestAmount)
    }

    private fun getText(item: Item): String {
        val (inventoryAmount, enderChestAmount) = amounts(item)
        return getText(item, inventoryAmount, enderChestAmount)
    }

    override fun draw(
        matrices: UMatrixStack,
        x: Float,
        y: Float,
        scale: Float,
        example: Boolean
    ) {
        if (
            !example
            && !HyBedWarsConfig.debugModeEnabled
            && (!HypixelPackets.inBedwars || (!showInLobby && HypixelPackets.inLobby))
        ) return

        val mc = Minecraft.getMinecraft()

        val iconSize = 16f
        val offset = iconSize + countersPadding

        fun measure(text: String): Int =
            mc.fontRendererObj.getStringWidth(text)

        val visibleItems = items().filter { showItem(it) }
        val texts = visibleItems.map { getText(it) }
        val textWidths = texts.map { measure(it) }
        val entryWidths = textWidths.map { it + iconPadding + iconSize }

        val size = visibleItems.size
        val horizontal = mode == 1

        if (size == 0) {
            actualWidth = 1f
            actualHeight = 1f
            return
        }

        val contentWidth: Float =
            if (horizontal) entryWidths.sum() + (size - 1) * countersPadding
            else entryWidths.maxOrNull() ?: 0f
        val contentHeight: Float =
            if (horizontal) iconSize
            else size * offset - countersPadding

        UGraphics.GL.pushMatrix()
        UGraphics.GL.scale(scale, scale, 1f)
        UGraphics.GL.translate(x / scale, y / scale, 0f)

        var cursorX = 0f
        for ((i, item) in visibleItems.withIndex()) {
            val stack = ItemStack(item)
            val text = texts[i]
            val textWidth = textWidths[i]
            val entryWidth = entryWidths[i]

            val entryX: Int
            val itemY: Int
            if (horizontal) {
                entryX = cursorX.toInt()
                itemY = 0
                cursorX += entryWidth + countersPadding
            } else {
                entryX = when (paddingSide) {
                    1 -> ((contentWidth - entryWidth) / 2f).toInt()
                    2 -> (contentWidth - entryWidth).toInt()
                    else -> 0
                }
                itemY = (i * offset).toInt()
            }

            val textX = entryX + if (iconSide == 1) 0 else (iconSize + iconPadding).toInt()
            val iconX = entryX + if (iconSide == 1) (textWidth + iconPadding).toInt()
            else 0

            RenderHelper.enableGUIStandardItemLighting()
            mc.renderItem.zLevel = 200f
            mc.renderItem.renderItemAndEffectIntoGUI(stack, iconX, itemY)
            mc.renderItem.renderItemOverlayIntoGUI(
                mc.fontRendererObj,
                stack,
                0,
                0,
                ""
            )
            RenderHelper.disableStandardItemLighting()

            RenderHelper.disableStandardItemLighting()

            TextRenderer.drawScaledString(
                text,
                textX.toFloat(),
                itemY + mc.fontRendererObj.FONT_HEIGHT / 2f,
                textColor.rgb,
                TextRenderer.TextType.toType(textType),
                1f
            )
        }

        UGraphics.GL.popMatrix()

        actualWidth = contentWidth
        actualHeight = contentHeight
    }

    override fun getWidth(scale: Float, example: Boolean): Float =
        actualWidth * scale

    override fun getHeight(scale: Float, example: Boolean): Float =
        actualHeight * scale

    override fun shouldShow(): Boolean =
        super.shouldShow() && (HyBedWarsConfig.debugModeEnabled || (HypixelPackets.inBedwars && (showInLobby || !HypixelPackets.inLobby)))
    *///?} else {
    private class Entry(val iconId: String, val text: String)

    private var entriesState: MutableState<List<Entry>> = mutableStateOf(emptyList())

    private fun entries(editing: Boolean): List<Entry> {
        if (!editing
            && !HyBedWarsConfig.debugModeEnabled
            && (!HypixelPackets.inBedwars || (!showInLobby && HypixelPackets.inLobby))
        ) return emptyList()

        return items().mapNotNull { item ->
            val (inventoryAmount, enderChestAmount) = amounts(item)

            if (!showItem(item, inventoryAmount, enderChestAmount)) null
            else Entry(iconId(item), getText(item, inventoryAmount, enderChestAmount).trimEnd())
        }
    }

    init {
        padLeft = 4f
        padRight = 4f
        padTop = 4f
        padBottom = 4f
        staticWidth = false
    }

    override val description: String = "Iron, gold, diamonds and emeralds counter display in Hypixel BedWars."

    override fun update(): Boolean {
        entriesState.value = entries(!isReal || HudManager.isEditing)
        return true
    }

    override fun updateFrequency(): Long = 50_000_000L

    override fun setup() {
        super.setup()
        update()
    }

    override fun hasBackground(): Boolean = entriesState.value.isNotEmpty()

    @Composable
    override fun Content() {
        val entries = entriesState.value
        if (entries.isEmpty()) return

        val scale = textScale
        val color = PolyColor(textColor, textChroma, textChromaSpeed)
        val shadow = showShadow
        val gap = countersPadding * scale

        PolyBox(modifier = hudBackground().padding(PolyInsets(padLeft, padTop, padRight, padBottom))) {
            if (mode == 1) {
                PolyRow(gap = gap) {
                    for (entry in entries) ResourceEntry(entry, color, shadow, scale)
                }
            } else {
                val widths = entries.map { entryWidth(it, scale) }
                val maxWidth = widths.maxOrNull() ?: 0f

                PolyColumn(gap = gap) {
                    for ((i, entry) in entries.withIndex()) {
                        val offsetX = when (paddingSide) {
                            1 -> (maxWidth - widths[i]) / 2f
                            2 -> maxWidth - widths[i]
                            else -> 0f
                        }
                        PolyBox(modifier = PolyModifier.size(maxWidth, ICON_SIZE * scale)) {
                            PolyBox(modifier = PolyModifier.absoluteAt(offsetX, 0f)) {
                                ResourceEntry(entry, color, shadow, scale)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun entryWidth(entry: Entry, scale: Float): Float =
        McFontQueue.measureTextWidth(entry.text, scale) + (iconPadding + ICON_SIZE) * scale

    @Composable
    private fun ResourceEntry(entry: Entry, color: PolyColor, shadow: Boolean, scale: Float) {
        PolyRow(gap = iconPadding * scale) {
            if (iconSide == 1) {
                ResourceText(entry.text, color, shadow, scale)
                PolyItemIcon(entry.iconId, ICON_SIZE * scale)
            } else {
                PolyItemIcon(entry.iconId, ICON_SIZE * scale)
                ResourceText(entry.text, color, shadow, scale)
            }
        }
    }

    @Composable
    private fun ResourceText(text: String, color: PolyColor, shadow: Boolean, scale: Float) {
        val width = McFontQueue.measureTextWidth(text, scale)
        val height = McFontQueue.measureTextHeight(scale)

        PolyBox(modifier = PolyModifier.size(width, ICON_SIZE * scale)) {
            PolyMcText(
                text,
                color = color,
                shadow = shadow,
                scale = scale,
                modifier = PolyModifier.absoluteAt(0f, (ICON_SIZE * scale - height) / 2f)
            )
        }
    }

    override fun minimumSize(): Pair<Float, Float> =
        (padLeft + padRight + 2f) to (padTop + padBottom + 2f)

    override fun defaultPosition(): Pair<Float, Float> = 0f to 0f
    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true

    override fun clone(): Hud = (super.clone() as BedwarsResourceDisplay).also {
        it.entriesState = mutableStateOf(entriesState.value)
    }
    //?}
}