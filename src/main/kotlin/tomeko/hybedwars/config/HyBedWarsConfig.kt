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
/*import tomeko.hybedwars.resources.*
import tomeko.hybedwars.location.GameModeHud
import tomeko.hybedwars.location.HeightLimitHud
import tomeko.hybedwars.location.MapNameHud
import tomeko.hybedwars.teammates.TeammateHud
*///?}
//? if 1.8.9
//import tomeko.hybedwars.restylers.ColoredBeds
import tomeko.hybedwars.utils.Constants
import tomeko.hybedwars.utils.NotifySounds

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
    //? if forge {
    /*@Exclude
    *///?}
    val DEPENDENCIES: List<Pair<String, List<String>>> = listOf(
        "notifyMiningFatigue" to listOf("disableNotifyMiningFatigueSkyblock"),
        "blockNotify" to listOf("blockNumber", "blockNotifySound"),
        "teammateDeathSound" to listOf("teammateDeathSoundType"),
    )

    fun register() {
        //? if forge
        //initialize()
        //? else
        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }

        //~ if forge addCallback -> addListener {
        //? if 1.8.9
        //addCallback("coloredBeds") { ColoredBeds.reloadRenderers() }
        addCallback("blockNotifySound") { NotifySounds.play(blockNotifySound, survivalOnly = false) }
        addCallback("teammateDeathSoundType") { NotifySounds.play(teammateDeathSoundType, survivalOnly = false) }
        //~}
    }

    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_RESOURCES = "Resources"

    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_SHOP = "Shop"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Use Middle Click in Shop",
        description = "Replace middle click with left click in BedWars item shop",
        category = CATEGORY_RESOURCES,
        subcategory = SUBCATEGORY_SHOP
    )
    var middleClickInBedwarsShop = true


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY = "Resource Display"

    //? if forge {
    /*@HUD(
        name = SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY,
        category = CATEGORY_RESOURCES
    )
    var bedwarsResourceDisplay = BedwarsResourceDisplay()
    *///?} else {
    @Info(
        title = "Resource Display can be edited by clicking Edit HUD in the top left corner",
        category = CATEGORY_RESOURCES,
        subcategory = SUBCATEGORY_BEDWARS_RESOURCE_DISPLAY
    )
    var bedwarsResourceDisplayInfo: Nothing? = null
    //?}


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_UPGRADES_HUD = "Upgrades HUD"

    //? if forge {
    /*@HUD(
        name = SUBCATEGORY_UPGRADES_HUD,
        category = CATEGORY_RESOURCES
    )
    var upgradesHUD = UpgradesHUD()
    *///?} else {
    @Info(
        title = "Upgrades HUD can be edited by clicking Edit HUD in the top left corner",
        category = CATEGORY_RESOURCES,
        subcategory = SUBCATEGORY_UPGRADES_HUD
    )
    var upgradesHUDInfo: Nothing? = null
    //?}


    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_TEAMMATES = "Teammates"

    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_DEATH_SOUND = "Death Sound"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Teammate Death Sound",
        description = "Plays a sound when a teammate dies in Hypixel BedWars.",
        category = CATEGORY_TEAMMATES,
        subcategory = SUBCATEGORY_DEATH_SOUND
    )
    var teammateDeathSound = false

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Teammate Death Sound Type",
        description = "Choose what sound to play when a teammate dies.",
        options = ["Hypixel Ding", "Golem Hit", "Blaze Hit", "Anvil Land", "Horse Death", "Ghast Scream", "Guardian Floop", "Cat Meow", "Dog Bark"],
        category = CATEGORY_TEAMMATES,
        subcategory = SUBCATEGORY_DEATH_SOUND
    )
    var teammateDeathSoundType = 1


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_TEAMMATES_HUD = "Teammates HUD"

    //? if forge {
    /*@HUD(
        name = "Teammates",
        category = CATEGORY_TEAMMATES,
        subcategory = SUBCATEGORY_TEAMMATES_HUD
    )
    var teammateHud = TeammateHud()
    *///?} else {
    @Info(
        title = "Teammates HUD can be added by clicking Edit HUD in the top left corner",
        category = CATEGORY_TEAMMATES,
        subcategory = SUBCATEGORY_TEAMMATES_HUD
    )
    var teammateHudInfo: Nothing? = null
    //?}


    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_LOCATION = "Location"

    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_BLOCK_NOTIFIER = "Block Notifier"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Notify When Blocks Run Out",
        description = "Pings you via a sound when your blocks are running out.",
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_BLOCK_NOTIFIER
    )
    var blockNotify = false

    @Slider(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Block Number",
        description = "Modify the number of blocks you (don't?) have for the Notify When Blocks Run Out feature to work.",
        min = 4f, max = 20f,
        //? if forge
        //step = 1,
        //? else
        step = 1f,
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_BLOCK_NOTIFIER
    )
    var blockNumber = 10

    @Dropdown(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Block Notify Sound",
        description = "Choose what sound to play.",
        options = ["Hypixel Ding", "Golem Hit", "Blaze Hit", "Anvil Land", "Horse Death", "Ghast Scream", "Guardian Floop", "Cat Meow", "Dog Bark"],
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_BLOCK_NOTIFIER
    )
    var blockNotifySound = 0


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_LOCATION_HUDS = "Location HUDs"

    //? if forge {
    /*@HUD(
        name = "Height Limit",
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_LOCATION_HUDS
    )
    var heightLimitHud = HeightLimitHud()

    @HUD(
        name = "Map Name",
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_LOCATION_HUDS
    )
    var mapNameHud = MapNameHud()

    @HUD(
        name = "Game Mode",
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_LOCATION_HUDS
    )
    var gameModeHud = GameModeHud()
    *///?} else {
    @Info(
        title = "Height Limit, Map Name and Game Mode HUDs can be added by clicking Edit HUD in the top left corner",
        category = CATEGORY_LOCATION,
        subcategory = SUBCATEGORY_LOCATION_HUDS
    )
    var infoHudsInfo: Nothing? = null
    //?}


    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_POTION_EFFECTS = "Potion Effects"

    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_MINING_FATIGUE = "Mining Fatigue"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Notify Mining Fatigue",
        description = "Send a notification when you get mining fatigue.",
        category = CATEGORY_POTION_EFFECTS,
        subcategory = SUBCATEGORY_MINING_FATIGUE
    )
    var notifyMiningFatigue = true

    @Checkbox(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Disable Mining Fatigue Notification in SkyBlock",
        description = "Disable the mining fatigue notification in SkyBlock.",
        category = CATEGORY_POTION_EFFECTS,
        subcategory = SUBCATEGORY_MINING_FATIGUE
    )
    var disableNotifyMiningFatigueSkyblock = true


    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_RESTYLERS = "Restylers"

    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_HEIGHT_OVERLAY = "Height Overlay"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Height Overlay",
        description = "Darken blocks on height limit in Hypixel BedWars",
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_HEIGHT_OVERLAY
    )
    var heightOverlay = true

    @Slider(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Opacity",
        description = "Set opacity of height overlay blocks darkening in Hypixel BedWars",
        min = 0f, max = 100f,
        //? if forge
        //step = 1,
        //? else
        step = 1f,
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_HEIGHT_OVERLAY
    )
    var heightOverlayOpacity = 67


    //? if forge {
    /*@Exclude
    *///?}
    //? if 1.8.9
    //private const val SUBCATEGORY_COLORED_BEDS = "Colored Beds"

    //? if forge {
    /*@Switch(
        name = "Colored Beds",
        description = "Make beds a different color depending on the team they are on.",
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_COLORED_BEDS
    )
    var coloredBeds = true
    *///?} elif ornithe {
    /*@Switch(
        title = "Colored Beds",
        description = "Make beds a different color depending on the team they are on.",
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_COLORED_BEDS
    )
    var coloredBeds = true
    *///?}


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_HEARTS = "Hardcore Hearts"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Hardcore Hearts When Bed Is Broken",
        description = "Show hardcore hearts after your bed is destroyed in Hypixel BedWars, as a reminder that you won't respawn.",
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_HEARTS
    )
    var hardcoreHearts = true


    //? if forge {
    /*@Exclude
    *///?}
    private const val SUBCATEGORY_HIDE_ARMOR = "Hide Armor"

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Hide Armor",
        description = "Hides Leather Armor",
        category = CATEGORY_RESTYLERS,
        subcategory = SUBCATEGORY_HIDE_ARMOR
    )
    var hideArmor = false


    //? if forge {
    /*@Exclude
    *///?}
    private const val CATEGORY_DEBUG = "Debug"

    @Info(
        //? if forge {
        /*text =
        *///?} else {
        title =
            //?}
            "Probably should stay disabled",
        //? if forge {
        /*type = InfoType.WARNING,
        *///?}
        category = CATEGORY_DEBUG,
    )
    var debugModeInfo: Nothing? = null

    @Switch(
        //? if forge {
        /*name =
        *///?} else {
        title =
            //?}
            "Debug Mode",
        category = CATEGORY_DEBUG,
    )
    var debugModeEnabled = false

    @Slider(
        //? if forge {
        /*name =
        *///?} else {
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
