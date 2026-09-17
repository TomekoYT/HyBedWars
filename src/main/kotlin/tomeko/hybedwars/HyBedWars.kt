package tomeko.hybedwars

//? if forge {
/*import cc.polyfrost.oneconfig.events.EventManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
*///?} elif ornithe {
//import net.ornithemc.osl.entrypoints.api.ModInitializer
//?} else {
import net.fabricmc.api.ClientModInitializer
//?}
import tomeko.hybedwars.commands.*
import tomeko.hybedwars.config.*
import tomeko.hybedwars.heightlimit.*
import tomeko.hybedwars.hud.*
import tomeko.hybedwars.location.*
import tomeko.hybedwars.utils.*

//? if forge {
/*@Mod(
    modid = Constants.MOD_ID,
    name = Constants.MOD_NAME,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter",
    dependencies = "required-after:hypixel_mod_api"
)
*///?}
class HyBedWars
//? if ornithe {
//: ModInitializer
//?} elif fabric {
    : ClientModInitializer
//?}
{
    //? if forge {
    //@Mod.EventHandler
    //?} else {
    override
    //?}
    fun
    //? if ornithe {
    //init(
    //?} else {
            onInitializeClient(
        //?}
        //? if forge {
        //event: FMLInitializationEvent
        //?}
    ) {
        //? if forge {
        //EventManager.INSTANCE.register(this)
        //?}

        HyBedWarsCommand.register()

        HyBedWarsConfig.register()

        HeightLimitData.register()
        HeightLimitRenderer.register()

        //? if !forge {
        BedwarsResourceDisplay.register()
        //?}

        HypixelPackets.register()

        ItemTracker.register()

        Debug.forceLog("${Constants.MOD_VERSION} Initialized!")
    }
}