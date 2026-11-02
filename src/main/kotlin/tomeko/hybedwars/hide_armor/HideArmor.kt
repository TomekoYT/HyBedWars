package tomeko.hybedwars.hide_armor

import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.utils.HypixelPackets

//? if 1.8.9 {
/*import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import java.util.Locale
*///?} else {
import net.minecraft.world.item.equipment.Equippable
import kotlin.jvm.optionals.getOrNull

//?}

object HideArmor {
    @JvmStatic
    //~ if 1.8.9 Equippable -> ItemStack
    fun shouldHideArmor(stack: Equippable?): Boolean {
        if (!HyBedWarsConfig.hideArmor)
            return false

        //? if 1.8.9 {
        /*val item = stack?.item
        
        if (item !is ItemArmor)
            return false
        
        val isLeather = item.armorMaterial == ItemArmor.ArmorMaterial.LEATHER
        *///?} else {
        val id = stack?.assetId()?.getOrNull() ?: return false
        val path = id.identifier().path

        val isLeather = path.startsWith("leather")
        //?}

        if (!HyBedWarsConfig.debugModeEnabled) {
            val mode = HypixelPackets.currentMode ?: return false

            // Don't hide armor if we're in a lobby,
            // I think Leather Armor can show up in it sometimes?
            return HypixelPackets.inGame
                    && HypixelPackets.inBedwars
                    && !mode.contains("LUCKY")
                    && isLeather
        } else {
            return isLeather
        }
    }
}
