package tomeko.hybedwars.restylers

//? if forge {
/*import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
*///?} elif ornithe {
/*import net.minecraft.util.IChatComponent as Component
import tomeko.hybedwars.event.ClientReceiveMessageEvents
*///?} else {
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
//?}
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.removeFormatting

object HardcoreHearts {
    private val BED_DESTROYED = Regex("^BED DESTRUCTION > Your Bed was ")

    @Volatile
    private var bedDestroyed = false
    private var gameServer: String? = null

    fun register() {
        //? if forge {
        /*MinecraftForge.EVENT_BUS.register(this)
        *///?} else {
        ClientReceiveMessageEvents.GAME.register(::onChat)
        //?}
    }

    fun shouldForce(): Boolean {
        if (!HyBedWarsConfig.hardcoreHearts) return false
        sync()
        return bedDestroyed
    }

    private fun sync() {
        if (!bedDestroyed) return

        val server = HypixelPackets.currentServerName
        if (!HyBedWarsConfig.debugModeEnabled && (!HypixelPackets.onHypixel || !HypixelPackets.inBedwars || !HypixelPackets.inGame || server != gameServer)) {
            bedDestroyed = false
            gameServer = null
        }
    }

    //? if forge {
    /*@SubscribeEvent
    *///?}
    fun onChat(
        //? if forge {
        /*event: ClientChatReceivedEvent
        *///?} else {
        component: Component,
        fromActionBar: Boolean
        //?}
    ) {
        //? if forge {
        /*if (event.type.toInt() == 2 || event.message == null) return
        *///?} else {
        if (fromActionBar) return
        //?}

        if (!HyBedWarsConfig.debugModeEnabled && (!HyBedWarsConfig.hardcoreHearts || !HypixelPackets.onHypixel || !HypixelPackets.inBedwars || !HypixelPackets.inGame)) return

        val message =
            //? if forge {
            /*event.message.unformattedText.removeFormatting()
            *///? elif ornithe {
            /*component.unformattedText.removeFormatting()
            *///?} else {
            component.string.removeFormatting()
            //?}

        if (BED_DESTROYED.containsMatchIn(message)) {
            bedDestroyed = true
            gameServer = HypixelPackets.currentServerName
        }
    }
}
