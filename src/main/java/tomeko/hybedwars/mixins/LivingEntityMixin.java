package tomeko.hybedwars.mixins;

//? if 1.8.9 {
/*import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
*///?} else {
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.hybedwars.miningfatigue.MiningFatigueNotifier;

@Mixin(
        //? if 1.8.9
        //EntityLivingBase.class
        //? else
        LivingEntity.class
)
abstract class LivingEntityMixin {
    //? if 1.8.9 {
    /*@Inject(method = "addPotionEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;onNewPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"))
    private void hybedwars$onEffectAdded(PotionEffect effect, CallbackInfo ci) {
        MiningFatigueNotifier.onEffectAdded(this, effect.getPotionID() == Potion.digSlowdown.getId());
    }
    *///?} else {
    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    private void hybedwars$onEffectAdded(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        MiningFatigueNotifier.onEffectAdded(this, effect.getEffect().equals(MobEffects.MINING_FATIGUE));
    }
    //?}
}
