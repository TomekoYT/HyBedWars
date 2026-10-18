package tomeko.hybedwars.miningfatigue

//? if forge {
/*import cc.polyfrost.oneconfig.utils.Notifications
*///?} else {
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
//?}
//? if 1.8.9 {
/*import net.minecraft.client.entity.EntityPlayerSP as LocalPlayer
*///?} else {
import net.minecraft.client.player.LocalPlayer
//?}
import net.hypixel.data.type.GameType
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.Constants

object MiningFatigueNotifier {
    @JvmStatic
    fun onEffectAdded(entity: Any, miningFatigue: Boolean) {
        if (HyBedWarsConfig.notifyMiningFatigue && miningFatigue && HypixelPackets.currentServerType != null && entity is LocalPlayer &&
            (!HyBedWarsConfig.disableNotifyMiningFatigueSkyblock || HypixelPackets.currentServerType != GameType.SKYBLOCK)
        ) {
            //? if forge {
            //Notifications.INSTANCE.send(Constants.MOD_NAME, "You have mining fatigue!")
            //?} else {
            Notifications.send(Constants.MOD_NAME, "You have mining fatigue!")
            //?}
        }
    }
}
