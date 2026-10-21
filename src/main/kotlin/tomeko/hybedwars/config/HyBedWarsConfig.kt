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
/*import tomeko.hybedwars.hud.BedwarsResourceDisplay
import tomeko.hybedwars.hud.GameModeHud
import tomeko.hybedwars.hud.HeightLimitHud
import tomeko.hybedwars.hud.MapNameHud
*///?}
//? if 1.8.9
//import tomeko.hybedwars.coloredbeds.ColoredBeds
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
        "notifyMiningFatigue" to listOf("disableNotifyMiningFatigueSkyblock"),
        "blockNotify" to listOf("blockNumber", "blockNotifySound"),
    )
    //?}

    fun register() {
        //? if forge {
        /*initialize()
        addDependency("disableNotifyMiningFatigueSkyblock", "notifyMiningFatigue")
        addDependency("blockNumber", "blockNotify")
        addDependency("blockNotifySound", "blockNotify")
        //? if 1.8.9
        addListener("coloredBeds") { ColoredBeds.reloadRenderers() }
        *///?} else {
        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }
        //? if 1.8.9
        //addCallback("coloredBeds", Runnable { ColoredBeds.reloadRenderers() })
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
    //? if 1.8.9
    //private const val SUBCATEGORY_COLORED_BEDS = "Colored Beds"

    //? if forge {
    /*@Switch(
        name = "Colored Beds",
        description = "Make beds a different color depending on the team they are on.",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_COLORED_BEDS
    )
    var coloredBeds = true
    *///?} elif 1.8.9 {
    /*@Switch(
        title = "Colored Beds",
        description = "Make beds a different color depending on the team they are on.",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_COLORED_BEDS
    )
    var coloredBeds = true
    *///?}

    //? if forge {
    //@Exclude
    //?}
    private const val SUBCATEGORY_POTION_EFFECTS = "Potion Effects"

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Notify Mining Fatigue",
        description = "Send a notification when you get mining fatigue.",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_POTION_EFFECTS
    )
    var notifyMiningFatigue = true

    @Checkbox(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Disable Mining Fatigue Notification in SkyBlock",
        description = "Disable the mining fatigue notification in SkyBlock.",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_POTION_EFFECTS
    )
    var disableNotifyMiningFatigueSkyblock = true

    //? if forge {
    //@Exclude
    //?}
    private const val SUBCATEGORY_SOUND = "Sound"

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Notify When Blocks Run Out",
        description = "Pings you via a sound when your blocks are running out.",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_SOUND
    )
    var blockNotify = false

    @Slider(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Block Number",
        description = "Modify the number of blocks you (don't?) have for the Notify When Blocks Run Out feature to work.",
        min = 4f, max = 20f,
        //? if forge
        //step = 1,
        //? else
        step = 1f,
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_SOUND
    )
    var blockNumber = 10

    @Dropdown(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Block Notify Sound",
        description = "Choose what sound to play.",
        options = ["Hypixel Ding", "Golem Hit", "Blaze Hit", "Anvil Land", "Horse Death", "Ghast Scream", "Guardian Floop", "Cat Meow", "Dog Bark"],
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_SOUND
    )
    var blockNotifySound = 0


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
    private const val SUBCATEGORY_INFO_HUDS = "Info HUDs"

    //? if forge {
    /*@HUD(
        name = "Height Limit",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_INFO_HUDS
    )
    var heightLimitHud = HeightLimitHud()

    @HUD(
        name = "Map Name",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_INFO_HUDS
    )
    var mapNameHud = MapNameHud()

    @HUD(
        name = "Game Mode",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_INFO_HUDS
    )
    var gameModeHud = GameModeHud()
    *///?} else {
    @Info(
        title = "Height Limit, Map Name and Game Mode HUDs can be added by clicking Edit HUD in the top left corner",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_INFO_HUDS
    )
    var infoHudsInfo: Nothing? = null
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

    @Slider(
        //? if forge {
        //name =
        //?} else {
        title =
            //?}
            "Debug Mode Height",
        min = 0f, max = 100f,
        //? if forge
        //step = 1,
        //? else
        step = 1f,
        category = CATEGORY_DEBUG
    )
    var debugModeHeight = 100
}