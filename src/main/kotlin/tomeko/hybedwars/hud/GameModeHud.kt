package tomeko.hybedwars.hud

//? if !forge {
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import tomeko.hybedwars.utils.Constants
//?}

/** Shows the current BedWars mode (Solo, Doubles, 4v4v4v4, ...) from the Hypixel Mod API. */
class GameModeHud : BedwarsTextHud("game_mode", "BedWars Game Mode", "Mode") {
    //? if !forge {
    companion object {
        fun register() {
            HudManager.register(GameModeHud(), Constants.MOD_ID, Constants.MOD_ICON)
        }
    }
    //?}

    override fun value(example: Boolean): String? =
        BedwarsInfo.modeName() ?: if (example) "4v4v4v4" else null
}
