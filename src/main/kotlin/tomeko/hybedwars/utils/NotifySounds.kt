package tomeko.hybedwars.utils

import net.minecraft.client.Minecraft
//? if !1.8.9 {
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.animal.feline.CatSoundVariants
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants
//?}

object NotifySounds {
    fun play(index: Int, survivalOnly: Boolean) {
        //? if 1.8.9 {
        /*val mc = Minecraft.getMinecraft()
        val player = mc.thePlayer ?: return
        if (survivalOnly && !mc.playerController.gameIsSurvivalOrAdventure()) return
        val sound = when (index) {
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
        player.playSound(sound, 1f, 1f)
        *///?} else {
        val mc = Minecraft.getInstance()
        if (survivalOnly && mc.gameMode?.canHurtPlayer() != true) return
        val sound = when (index) {
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
