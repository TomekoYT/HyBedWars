package tomeko.hybedwars.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import tomeko.hybedwars.hide_armor.HideArmor;

//? if 1.8.9 {
/*
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?} else {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
//?}

@Mixin(
    //? if 1.8.9
    //LayerArmorBase.class
    //? else
    HumanoidMobRenderer.class
)
abstract class HideIngameArmorHumanoidArmorLayerMixin {
    //? if 1.8.9 {
    /*
    @Shadow
    public abstract ItemStack getCurrentArmor(EntityLivingBase entityLivingBaseIn, int armorSlot);

    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/LayerArmorBase;getArmorModel(I)Lnet/minecraft/client/model/ModelBase;"), cancellable = true)
    private void hybedwars$cancelArmor(EntityLivingBase entityLivingBaseIn, float f, float g, float partialTicks, float h, float i, float j, float scale, int armorSlot, CallbackInfo ci) {
        if (!entityLivingBaseIn.isInvisible() && HideArmor.shouldHideArmor(getCurrentArmor(entityLivingBaseIn, armorSlot))) {
            ci.cancel();
        }
    }
    *///?} else {
    @ModifyExpressionValue(
            method = "getEquipmentIfRenderable",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;shouldRender(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z"
            )
    )
    private static boolean hybedwars$shouldRenderArmorPiece(boolean shouldRender, @Local ItemStack itemStack) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        return shouldRender && !HideArmor.shouldHideArmor(equippable);
    }
    //?}
}
