package tomeko.hybedwars.location

//? if 1.8.9 {
/*import net.minecraft.block.BlockTNT as TntBlock
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemBlock as BlockItem
import tomeko.hybedwars.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.TntBlock
//?}
import net.hypixel.data.type.GameType
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.utils.NotifySounds

object BlockNotifier {
    private var ticks = -1

    fun register() {
        ClientTickEvents.START_CLIENT_TICK.register { onTick() }
    }

    private fun onTick() {
        //? if 1.8.9 {
        /*val mc = Minecraft.getMinecraft()
        if (mc.thePlayer == null || mc.theWorld == null) return
        val stack = mc.thePlayer.currentEquippedItem ?: return
        val count = stack.stackSize
        *///?} else {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        if (mc.level == null) return
        val stack = player.mainHandItem
        if (stack.isEmpty) return
        val count = stack.count
        //?}

        if (!HypixelPackets.onHypixel || !HyBedWarsConfig.blockNotify || HypixelPackets.currentServerType == null || !HypixelPackets.inGame) return

        when (HypixelPackets.currentServerType) {
            GameType.BUILD_BATTLE, GameType.HOUSING, GameType.SKYBLOCK -> return
            else -> {}
        }

        val item = stack.item
        if (item is BlockItem && item.block !is TntBlock && count <= HyBedWarsConfig.blockNumber && count > 4) {
            ticks++
            if (ticks == 0 || ticks == 20) {
                playSound()
                return
            }
            if (ticks > 40) ticks = -1
        } else if (ticks != -1) {
            ticks = -1
        }
    }

    private fun playSound() {
        NotifySounds.play(HyBedWarsConfig.blockNotifySound, survivalOnly = true)
    }
}
