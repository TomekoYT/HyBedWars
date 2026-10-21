package tomeko.hybedwars.hud

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.Switch
*///?} else {
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import tomeko.hybedwars.utils.Constants
//?}

/** Shows how many blocks are left until the map's build height limit. */
class HeightLimitHud : BedwarsTextHud("height_limit", "BedWars Height Limit", "Height Limit") {
    //? if !forge {
    companion object {
        fun register() {
            HudManager.register(HeightLimitHud(), Constants.MOD_ID, Constants.MOD_ICON)
        }
    }
    //?}

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Show Limit Y",
        //? if !forge {
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_OPTIONS
        //?}
    )
    var showLimitY = false

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Color By Distance",
        //? if !forge {
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_OPTIONS
        //?}
    )
    var colorByDistance = true

    private fun blocksLeft(example: Boolean): Int? =
        BedwarsInfo.blocksUntilHeightLimit() ?: if (example) 12 else null

    override fun value(example: Boolean): String? {
        val blocks = blocksLeft(example) ?: return null

        if (!showLimitY) return blocks.toString()

        val limitY = BedwarsInfo.maxBuildY() ?: (if (example) 89 else null)
        return if (limitY == null) blocks.toString() else "$blocks (Y $limitY)"
    }

    override fun valueColor(example: Boolean): Int? {
        if (!colorByDistance) return null
        val blocks = blocksLeft(example) ?: return null

        return when {
            blocks <= 3 -> COLOR_BAD
            blocks <= 10 -> COLOR_WARN
            else -> COLOR_GOOD
        }
    }
}
