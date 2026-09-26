package tomeko.hybedwars.config

//? if forge {
/*import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor as PolyColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
*///?} else {
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
//?}
//? if forge {
//import tomeko.hybedwars.hud.BedwarsResourceDisplay
//?}
import tomeko.hybedwars.utils.Constants

object HyBedWarsConfig : Config(
    //? if forge {
    /*Mod(
        Constants.MOD_NAME,
        ModType.HYPIXEL,
        Constants.MOD_ICON
    ),
    "${Constants.MOD_ID}.json"
    *///?} else {
    "${Constants.MOD_ID}.json",
    Constants.MOD_ICON,
    Constants.MOD_NAME,
    Category.HYPIXEL
    //?}
) {
    //? if !forge {
    val DEPENDENCIES: List<Pair<String, List<String>>> = listOf(

    )
    //?}

    fun register() {
        //? if forge {
        //initialize()
        //?} else {
        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }
        //?}
    }

    //? if forge {
    //@Exclude
    //?}
    private const val CATEGORY_GENERAL = "General"

    //? if forge {
    //@Exclude
    //?}
    private const val SUBCATEGORY_HEIGHT_OVERLAY = "Height Overlay"

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Height Overlay",
        description = "Darken blocks on height limit in Hypixel BedWars",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_HEIGHT_OVERLAY
    )
    var heightOverlay = true

    @Slider(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Opacity",
        description = "Set opacity of height overlay blocks darkening in Hypixel BedWars",
        min = 0f, max = 100f,
        //? forge
        //step = 1,
        //? else
        step = 1f,
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_HEIGHT_OVERLAY
    )
    var heightOverlayOpacity = 67

    //? if forge {
    //@Exclude
    //?}
    private const val SUBCATEGORY_SHOP = "Shop"

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Use Middle Click in Shop",
        description = "Replace middle click with left click in BedWars item shop",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_SHOP
    )
    var middleClickInBedwarsShop = true


    //? if forge {
    //@Exclude
    //?}
    private const val SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY = "Resource Display"

    //? if forge {
    /*@HUD(
        name = SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY,
        category = CATEGORY_GENERAL
    )
    var bedwarsResourceDisplay = BedwarsResourceDisplay()
    *///?} else {
    @Info(
        title = "Resource Display can be edited by clicking Edit HUD in the top left corner",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY
    )
    var bedwarsResourceDisplayInfo: Nothing? = null
    //?}


    //? if forge {
    //@Exclude
    //?}
    private const val CATEGORY_DEBUG = "Debug"

    @Info(
        //? if forge {
        //text =
        //?} else {
        title =
            //?}
            "Probably should stay disabled",
        //? if forge {
        //type = InfoType.WARNING,
        //?}
        category = CATEGORY_DEBUG,
    )
    var debugModeInfo: Nothing? = null

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Debug Mode",
        category = CATEGORY_DEBUG,
    )
    var debugModeEnabled = false
}