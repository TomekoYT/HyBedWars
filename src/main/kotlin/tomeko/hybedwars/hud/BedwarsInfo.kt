package tomeko.hybedwars.hud

import net.minecraft.client.Minecraft
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.heightlimit.HeightLimitData
import tomeko.hybedwars.location.HypixelPackets
import java.util.Locale
import kotlin.math.floor

/**
 * Game info shown by the text HUDs.
 * Map and mode come from the Hypixel Mod API location packet,
 * the height limit comes from the same data file the height overlay uses.
 */
object BedwarsInfo {
    // Names from https://api.hypixel.net/v2/resources/games
    private val MODE_NAMES = mapOf(
        "BEDWARS_EIGHT_ONE" to "Solo",
        "BEDWARS_EIGHT_TWO" to "Doubles",
        "BEDWARS_FOUR_THREE" to "3v3v3v3",
        "BEDWARS_FOUR_FOUR" to "4v4v4v4",
        "BEDWARS_TWO_FOUR" to "4v4",
        "BEDWARS_CASTLE" to "Castle",
        "BEDWARS_PRACTICE" to "Practice",
        "BEDWARS_TWO_ONE_DUELS" to "Bed Wars Duel",
        "BEDWARS_TWO_ONE_DUELS_RUSH" to "Bed Rush Duel",
        "BEDWARS_EIGHT_ONE_ONEBLOCK" to "One Block",
        "BEDWARS_EIGHT_ONE_RUSH" to "Rush Solo",
        "BEDWARS_EIGHT_TWO_RUSH" to "Rush Doubles",
        "BEDWARS_FOUR_FOUR_RUSH" to "Rush 4v4v4v4",
        "BEDWARS_EIGHT_ONE_ULTIMATE" to "Ultimate Solo",
        "BEDWARS_EIGHT_TWO_ULTIMATE" to "Ultimate Doubles",
        "BEDWARS_FOUR_FOUR_ULTIMATE" to "Ultimate 4v4v4v4",
        "BEDWARS_EIGHT_TWO_LUCKY" to "Lucky Doubles",
        "BEDWARS_FOUR_FOUR_LUCKY" to "Lucky 4v4v4v4",
        "BEDWARS_EIGHT_TWO_VOIDLESS" to "Voidless Doubles",
        "BEDWARS_FOUR_FOUR_VOIDLESS" to "Voidless 4v4v4v4",
        "BEDWARS_EIGHT_TWO_ARMED" to "Armed Doubles",
        "BEDWARS_FOUR_FOUR_ARMED" to "Armed 4v4v4v4",
        "BEDWARS_EIGHT_TWO_SWAP" to "Swappage Doubles",
        "BEDWARS_FOUR_FOUR_SWAP" to "Swappage 4v4v4v4",
        "BEDWARS_EIGHT_TWO_UNDERWORLD" to "Underworld Doubles",
        "BEDWARS_FOUR_FOUR_UNDERWORLD" to "Underworld 4v4v4v4",
        "BEDWARS_EIGHT_TWO_TOURNEY" to "Doubles (Tournament)",
        "BEDWARS_FOUR_FOUR_TOURNEY" to "4v4v4v4 (Tournament)",
        "BEDWARS_TWO_FOUR_TOURNEY" to "4v4 (Tournament)",
        "LOBBY" to "Lobby"
    )

    /** True when the HUDs should draw outside the HUD editor. */
    val active: Boolean
        get() = HyBedWarsConfig.debugModeEnabled || HypixelPackets.inBedwars

    /** First Y level where blocks can no longer be placed, or null if unknown for this map. */
    fun maxBuildY(): Int? {
        // Debug height is the darkened layer, which is the last placeable one
        if (HyBedWarsConfig.debugModeEnabled) return HyBedWarsConfig.debugModeHeight + 1

        val map = HypixelPackets.currentMapName ?: return null
        return HeightLimitData.getBedwarsLimits(map)?.maxBuild
    }

    /** How many more blocks the player can stack under themselves before hitting the limit. */
    fun blocksUntilHeightLimit(): Int? {
        val maxBuild = maxBuildY() ?: return null

        val mc =
            //? if 1.8.9 {
            /*Minecraft.getMinecraft()
            *///?} else {
            Minecraft.getInstance()
            //?}

        val feetY =
            //? if 1.8.9 {
            /*floor(mc.thePlayer?.posY ?: return null).toInt()
            *///?} else {
            floor(mc.player?.y ?: return null).toInt()
            //?}

        return (maxBuild - feetY).coerceAtLeast(0)
    }

    fun mapName(): String? = HypixelPackets.currentMapName?.takeIf { it.isNotBlank() }

    fun modeName(): String? {
        val raw = HypixelPackets.currentMode?.takeIf { it.isNotBlank() } ?: return null
        return prettyMode(raw)
    }

    /** Turns a mode id like BEDWARS_FOUR_FOUR into a readable name, with a fallback for new modes. */
    fun prettyMode(raw: String): String {
        MODE_NAMES[raw.uppercase(Locale.ROOT)]?.let { return it }

        return raw.uppercase(Locale.ROOT)
            .removePrefix("BEDWARS_")
            .split('_')
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase(Locale.ROOT) }
            }
    }
}
