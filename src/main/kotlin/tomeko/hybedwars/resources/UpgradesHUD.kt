package tomeko.hybedwars.resources

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.hud.TextHud
*///?} else {
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
//?}
//? if forge {
/*import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
*///?} elif ornithe {
//import net.minecraft.util.IChatComponent as Component
//import tomeko.hybedwars.event.ClientReceiveMessageEvents
//import tomeko.hybedwars.event.ClientTickEvents
//?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
//?}
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.Constants
import tomeko.hybedwars.utils.removeFormatting
import java.util.Locale

private const val COLOR_CODES = "0123456789abcdef"

private val COLOR_ARGB = intArrayOf(
    0xFF000000.toInt(), 0xFF0000AA.toInt(), 0xFF00AA00.toInt(), 0xFF00AAAA.toInt(),
    0xFFAA0000.toInt(), 0xFFAA00AA.toInt(), 0xFFFFAA00.toInt(), 0xFFAAAAAA.toInt(),
    0xFF555555.toInt(), 0xFF5555FF.toInt(), 0xFF55FF55.toInt(), 0xFF55FFFF.toInt(),
    0xFFFF5555.toInt(), 0xFFFF55FF.toInt(), 0xFFFFFF55.toInt(), 0xFFFFFFFF.toInt()
)

private val GRAY_ARGB = COLOR_ARGB[7]

class UpgradesLine(val kind: Kind, val text: String = "", val level: String? = null) {
    enum class Kind { HEADING, ENTRY, BLANK }
}

class BedwarsUpgradesService {
    private var sharpSwords = 0
    private var reinforcedArmor = 0
    private var maniacMiner = 0
    private var cushionedBoots = 0
    private var forgeLevel = 0
    private var healPool = false
    private var deadShot = 0
    private val activeTraps = ArrayList<String>()

    companion object {
        private val FORGE_LEVELS = arrayOf("", "Iron Forge", "Golden Forge", "Emerald Forge", "Molten Forge")

        private val PLAYER_CHAT = Regex("^(?:\\[.*?\\]\\s*)*(\\w{3,16})(?::| ») .*$")
        private val TRAP_SET_OFF = Regex("^(.+?)\\s+Trap was set off!$", RegexOption.IGNORE_CASE)
        private val TRAP_REMOVED = Regex("^Removed\\s+(.+?)\\s+Trap from the queue!$", RegexOption.IGNORE_CASE)

        fun isStartMessage(message: String): Boolean =
            message.contains("Protect your bed and destroy the enemy beds.")
                    && !message.contains(":")
                    && !message.contains("SHOUT")

        fun isCountdownMessage(message: String): Boolean =
            message.startsWith("The game starts in ") && message.contains(" seconds!")

        private fun isPlayerChat(message: String): Boolean = PLAYER_CHAT.matches(message)

        fun isPurchaseMessage(message: String): Boolean =
            !isPlayerChat(message) && message.lowercase(Locale.ROOT).contains("purchased")

        fun isTrapSignalMessage(message: String): Boolean {
            if (isPlayerChat(message)) return false
            val lower = message.lowercase(Locale.ROOT)
            return lower.contains("trap was set off!")
                    || lower.contains("reveal trap set off")
                    || (lower.contains("removed") && lower.contains("trap from the queue"))
        }

        fun example(): BedwarsUpgradesService = BedwarsUpgradesService().also {
            it.sharpSwords = 2
            it.reinforcedArmor = 3
            it.healPool = true
            it.activeTraps.add("Counter-Offensive Trap")
            it.activeTraps.add("Blindness Trap")
        }
    }

    @Synchronized
    fun reset() {
        sharpSwords = 0
        reinforcedArmor = 0
        maniacMiner = 0
        cushionedBoots = 0
        forgeLevel = 0
        healPool = false
        deadShot = 0
        activeTraps.clear()
    }

    @Synchronized
    fun processPurchaseMessage(message: String) {
        val cleaned = message.removeFormatting().trim()
        if (!isPurchaseMessage(cleaned)) return

        val parts = cleaned.split("purchased")
        if (parts.size < 2) return

        val item = parts[1]
            .trim { it.isWhitespace() || it == '"' || it == '\'' || it == '`' }
            .trimEnd('.', '!', '?')
            .trim()
        val lower = item.lowercase(Locale.ROOT)

        when {
            lower.contains("trap") -> activeTraps.add(item)
            lower.startsWith("sharpened swords") -> sharpSwords = maxOf(sharpSwords, extractLevel(item))
            lower.startsWith("reinforced armor") -> reinforcedArmor = maxOf(reinforcedArmor, extractLevel(item))
            lower.startsWith("maniac miner") -> maniacMiner = maxOf(maniacMiner, extractLevel(item))
            lower.startsWith("cushioned boots") -> cushionedBoots = maxOf(cushionedBoots, extractLevel(item))
            lower.startsWith("heal pool") -> healPool = true
            lower.startsWith("deadshot") -> deadShot = maxOf(deadShot, extractLevel(item))
            else -> {
                for (i in 1 until FORGE_LEVELS.size) {
                    if (item.equals(FORGE_LEVELS[i], ignoreCase = true)) {
                        forgeLevel = maxOf(forgeLevel, i)
                        return
                    }
                }
            }
        }
    }

    @Synchronized
    fun processTrapTriggeredMessage(message: String) {
        val cleaned = message.removeFormatting().trim()

        TRAP_SET_OFF.matchEntire(cleaned)?.let {
            removeTrapFromQueue(it.groupValues[1].trim() + " Trap")
            return
        }

        if (cleaned.lowercase(Locale.ROOT).contains("reveal trap set off")) {
            removeTrapFromQueue("Reveal Trap")
            return
        }

        TRAP_REMOVED.matchEntire(cleaned)?.let {
            removeTrapFromQueue(it.groupValues[1].trim() + " Trap")
        }
    }

    private fun removeTrapFromQueue(trapName: String) {
        val index = activeTraps.indexOfFirst { it.equals(trapName, ignoreCase = true) }
        if (index >= 0) activeTraps.removeAt(index)
    }

    private fun extractLevel(item: String): Int =
        when (val last = item.trim().substringAfterLast(' ')) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            else -> last.toIntOrNull()?.takeIf { it > 0 } ?: 1
        }

    @Synchronized
    fun getDisplayLines(shortNames: Boolean, romanNumerals: Boolean): List<UpgradesLine> {
        val lines = ArrayList<UpgradesLine>()

        fun level(value: Int): String = if (romanNumerals) romanNumeral(value) else value.toString()

        lines.add(UpgradesLine(UpgradesLine.Kind.HEADING, "Upgrades:"))

        if (sharpSwords >= 2) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("Sharpened Swords", shortNames), level(sharpSwords)))
        } else if (sharpSwords == 1) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("Sharpened Swords", shortNames)))
        }

        if (reinforcedArmor > 0) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("Reinforced Armor", shortNames), level(reinforcedArmor)))
        }

        if (maniacMiner > 0) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("Maniac Miner", shortNames), level(maniacMiner)))
        }

        if (cushionedBoots > 0) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("Cushioned Boots", shortNames), level(cushionedBoots)))
        }

        if (forgeLevel > 0 && forgeLevel < FORGE_LEVELS.size) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, FORGE_LEVELS[forgeLevel]))
        }

        if (healPool) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, "Heal Pool"))
        }

        if (deadShot > 0) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, shortName("deadShot", shortNames), level(deadShot)))
        }

        if (sharpSwords == 0 && reinforcedArmor == 0 && maniacMiner == 0 && cushionedBoots == 0
            && forgeLevel == 0 && !healPool && deadShot == 0
        ) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, "None"))
        }

        lines.add(UpgradesLine(UpgradesLine.Kind.BLANK))
        lines.add(UpgradesLine(UpgradesLine.Kind.HEADING, "Traps:"))

        if (activeTraps.isEmpty()) {
            lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, "None"))
        } else {
            for (trap in activeTraps) {
                lines.add(UpgradesLine(UpgradesLine.Kind.ENTRY, if (shortNames) shortTrapName(trap) else trap))
            }
        }

        return lines
    }

    private fun shortName(fullName: String, short: Boolean): String {
        if (!short) return fullName
        return when (fullName) {
            "Sharpened Swords" -> "Sharp"
            "Reinforced Armor" -> "Prot"
            "Cushioned Boots" -> "FF"
            "Maniac Miner" -> "Haste"
            else -> fullName
        }
    }

    private fun shortTrapName(fullName: String): String {
        var name = fullName
        if (name.lowercase(Locale.ROOT).endsWith(" trap")) {
            name = name.substring(0, name.length - 5).trim()
        }

        return when (name) {
            "Miner Fatigue", "Mining Fatigue" -> "Fatigue"
            "Reveal", "Alarm" -> "Reveal"
            "Counter-Offensive", "Counter Offensive", "Counter Offense" -> "Jump"
            "Blindness" -> "Blind"
            "It's a Trap!", "It's a Trap" -> "Trap"
            else -> {
                val lower = name.lowercase(Locale.ROOT)
                when {
                    lower.contains("fatigue") -> "Fatigue"
                    lower.contains("reveal") || lower.contains("alarm") -> "Reveal"
                    lower.contains("counter") -> "Jump"
                    lower.contains("blind") -> "Blind"
                    else -> name
                }
            }
        }
    }

    private fun romanNumeral(number: Int): String =
        when (number) {
            1 -> "I"
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            else -> number.toString()
        }
}

object BedwarsUpgradesTracker {
    val service = BedwarsUpgradesService()

    private var registered = false
    private var lastServerName: String? = null

    fun register() {
        if (registered) return
        registered = true

        //? if forge {
        //MinecraftForge.EVENT_BUS.register(this)
        //?} else {
        ClientReceiveMessageEvents.GAME.register(::onGameMessage)
        ClientTickEvents.START_CLIENT_TICK.register { tick() }
        //?}
    }

    val inSession: Boolean
        get() = HyBedWarsConfig.debugModeEnabled || (HypixelPackets.inBedwars && HypixelPackets.inGame)

    //? if forge {
    /*@SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        // 2 is the action bar
        if (event.type.toInt() == 2 || event.message == null) return
        handleMessage(event.message.unformattedText)
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        tick()
    }
    *///?} else {
    private fun onGameMessage(component: Component, fromActionBar: Boolean) {
        if (fromActionBar) return

        handleMessage(
            //? if ornithe {
            //component.unformattedText
            //?} else {
            component.string
            //?}
        )
    }
    //?}

    private fun handleMessage(raw: String) {
        val message = raw.removeFormatting().trim()

        if (BedwarsUpgradesService.isStartMessage(message) || BedwarsUpgradesService.isCountdownMessage(message)) {
            service.reset()
        }

        if (!inSession) return

        if (BedwarsUpgradesService.isPurchaseMessage(message)) {
            service.processPurchaseMessage(message)
        }

        if (BedwarsUpgradesService.isTrapSignalMessage(message)) {
            service.processTrapTriggeredMessage(message)
        }
    }

    private fun tick() {
        if (!inSession) {
            service.reset()
            lastServerName = null
            return
        }

        // A new game is a new server
        val server = HypixelPackets.currentServerName
        if (server != lastServerName) {
            lastServerName = server
            service.reset()
        }
    }
}

class UpgradesHUD
//? if forge {
//: TextHud(false)
//?} else {
    : Hud("${Constants.MOD_ID}_bedwars_upgrades_traps.json", "BedWars Upgrades & Traps", Category.COMBAT)
//?}
{
    //? if forge
    //@Exclude
    companion object {
        //? if !forge {
        fun register() {
            BedwarsUpgradesTracker.register()
            HudManager.register(UpgradesHUD(), Constants.MOD_ID, Constants.MOD_ICON)
        }

        private const val SUBCATEGORY_GENERAL = "General"
        private const val SUBCATEGORY_FORMATTING = "Formatting"
        //?}
    }

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Short Names",
        description = "Use short names (Sharp, Prot, FF, Haste, etc.)",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var shortNames = false

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Roman Numerals",
        description = "Use Roman numerals (I, II, III, IV) instead of numbers",
        //? if !forge {
        subcategory = SUBCATEGORY_GENERAL
        //?}
    )
    var romanNumerals = true

    @Dropdown(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Heading Color",
        description = "Color for section headings (Upgrades/Traps)",
        options = [
            "Black", "Dark Blue", "Dark Green", "Dark Aqua",
            "Dark Red", "Dark Purple", "Gold", "Gray",
            "Dark Gray", "Blue", "Green", "Aqua",
            "Red", "Light Purple", "Yellow", "White"
        ],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var headingColorIndex = 11

    @Dropdown(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Text Color",
        description = "Color for upgrade and trap names",
        options = [
            "Black", "Dark Blue", "Dark Green", "Dark Aqua",
            "Dark Red", "Dark Purple", "Gold", "Gray",
            "Dark Gray", "Blue", "Green", "Aqua",
            "Red", "Light Purple", "Yellow", "White"
        ],
        //? if !forge {
        subcategory = SUBCATEGORY_FORMATTING
        //?}
    )
    var textColorIndex = 15

    init {
        BedwarsUpgradesTracker.register()
    }

    private val headingIndex: Int
        get() = headingColorIndex.coerceIn(0, COLOR_CODES.length - 1)

    private val bodyIndex: Int
        get() = textColorIndex.coerceIn(0, COLOR_CODES.length - 1)

    private val active: Boolean
        get() = BedwarsUpgradesTracker.inSession

    private fun buildLines(example: Boolean): List<UpgradesLine> {
        if (!example && !active) return emptyList()

        val service = if (example) BedwarsUpgradesService.example() else BedwarsUpgradesTracker.service
        return service.getDisplayLines(shortNames, romanNumerals)
    }

    //? if forge {
    /*init {
        // Text shadow
        textType = 1
    }

    override fun getLines(lines: MutableList<String>, example: Boolean) {
        val heading = "§" + COLOR_CODES[headingIndex]
        val body = "§" + COLOR_CODES[bodyIndex]

        for (line in buildLines(example)) {
            lines.add(
                when (line.kind) {
                    UpgradesLine.Kind.HEADING -> "${heading}§l${line.text}"
                    UpgradesLine.Kind.ENTRY ->
                        if (line.level != null) "${body}${line.text} §7${line.level}" else "${body}${line.text}"
                    UpgradesLine.Kind.BLANK -> ""
                }
            )
        }
    }

    override fun shouldShow(): Boolean = super.shouldShow() && active
    *///?} else {
    private var linesState: MutableState<List<UpgradesLine>> = mutableStateOf(buildLines(true))

    init {
        padLeft = 4f
        padRight = 4f
        padTop = 4f
        padBottom = 4f
        staticWidth = false
    }

    override val description: String = "The team upgrades and traps you bought in Hypixel BedWars."

    override fun update(): Boolean {
        linesState.value = buildLines(!isReal || HudManager.isEditing)
        return true
    }

    override fun updateFrequency(): Long = 50_000_000L

    override fun setup() {
        super.setup()
        update()
    }

    override fun hasBackground(): Boolean = linesState.value.isNotEmpty()

    @Composable
    override fun Content() {
        val lines = linesState.value
        if (lines.isEmpty()) return

        val scale = textScale
        val shadow = showShadow
        val headingColor = PolyColor(COLOR_ARGB[headingIndex])
        val bodyColor = PolyColor(COLOR_ARGB[bodyIndex])
        val levelColor = PolyColor(GRAY_ARGB)
        val lineHeight = McFontQueue.measureTextHeight(scale)
        val spaceWidth = McFontQueue.measureTextWidth(" ", scale)

        PolyBox(modifier = hudBackground().padding(PolyInsets(padLeft, padTop, padRight, padBottom))) {
            PolyColumn(gap = 0f) {
                for (line in lines) {
                    when (line.kind) {
                        UpgradesLine.Kind.HEADING ->
                            PolyMcText("§l" + line.text, color = headingColor, shadow = shadow, scale = scale)

                        UpgradesLine.Kind.ENTRY ->
                            PolyRow(gap = spaceWidth) {
                                PolyMcText(line.text, color = bodyColor, shadow = shadow, scale = scale)
                                if (line.level != null) {
                                    PolyMcText(line.level, color = levelColor, shadow = shadow, scale = scale)
                                }
                            }

                        UpgradesLine.Kind.BLANK ->
                            PolyBox(modifier = PolyModifier.size(1f, lineHeight)) {}
                    }
                }
            }
        }
    }

    override fun minimumSize(): Pair<Float, Float> =
        (padLeft + padRight + 2f) to (padTop + padBottom + 2f)

    override fun defaultPosition(): Pair<Float, Float> = 5f to 65f
    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true

    override fun clone(): Hud = (super.clone() as UpgradesHUD).also {
        it.linesState = mutableStateOf(linesState.value)
    }
    //?}
}