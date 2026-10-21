package tomeko.hybedwars.hud

//? if !forge {
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import tomeko.hybedwars.utils.Constants
//?}

/** Shows the current BedWars map name from the Hypixel Mod API. */
class MapNameHud : BedwarsTextHud("map_name", "BedWars Map Name", "Map") {
    //? if !forge {
    companion object {
        fun register() {
            HudManager.register(MapNameHud(), Constants.MOD_ID, Constants.MOD_ICON)
        }
    }
    //?}

    override fun value(example: Boolean): String? =
        BedwarsInfo.mapName() ?: if (example) "Lighthouse" else null
}
