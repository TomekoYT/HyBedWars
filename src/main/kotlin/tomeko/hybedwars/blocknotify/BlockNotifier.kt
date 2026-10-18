package tomeko.hybedwars.blocknotify

//? if 1.8.9 {
/*import net.minecraft.block.BlockTNT as TntBlock
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemBlock as BlockItem
import tomeko.hybedwars.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.animal.feline.CatSoundVariants
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.TntBlock
//?}
import net.hypixel.data.type.GameType
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets

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
        //? if 1.8.9 {
        /*val mc = Minecraft.getMinecraft()
        if (!mc.playerController.gameIsSurvivalOrAdventure()) return
        val sound = when (HyBedWarsConfig.blockNotifySound) {
            0 -> "random.orb"
            1 -> "mob.irongolem.hit"
            2 -> "mob.blaze.hit"
            3 -> "random.anvil_land"
            4 -> "mob.horse.death"
            5 -> "mob.ghast.scream"
            6 -> "mob.guardian.land.hit"
            7 -> "mob.cat.meow"
            8 -> "mob.wolf.bark"
            else -> return
        }
        mc.thePlayer.playSound(sound, 1f, 1f)
        *///?} else {
        val mc = Minecraft.getInstance()
        if (mc.gameMode?.canHurtPlayer() != true) return
        val sound = when (HyBedWarsConfig.blockNotifySound) {
            0 -> SoundEvents.EXPERIENCE_ORB_PICKUP
            1 -> SoundEvents.IRON_GOLEM_HURT
            2 -> SoundEvents.BLAZE_HURT
            3 -> SoundEvents.ANVIL_LAND
            4 -> SoundEvents.HORSE_DEATH
            5 -> SoundEvents.GHAST_SCREAM
            6 -> SoundEvents.GUARDIAN_HURT_LAND
            7 -> SoundEvents.CAT_SOUNDS[CatSoundVariants.SoundSet.CLASSIC]?.adultSounds?.ambientSound?.value()
            8 -> SoundEvents.WOLF_SOUNDS[WolfSoundVariants.SoundSet.CLASSIC]?.adultSounds?.ambientSound?.value()
            else -> null
        } ?: return
        mc.player?.playSound(sound, 1f, 1f)
        //?}
    }
}
