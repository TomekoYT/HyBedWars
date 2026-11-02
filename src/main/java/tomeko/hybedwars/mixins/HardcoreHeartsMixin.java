package tomeko.hybedwars.mixins;

//? if 1.8.9
//import net.minecraft.world.storage.WorldInfo;
//? else
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.hybedwars.restylers.HardcoreHearts;

@Mixin(
        //? if 1.8.9
        //WorldInfo.class
        //? else
        ClientLevelData.class
)
abstract class HardcoreHeartsMixin {
    @Inject(
            method =
                    //? if 1.8.9
                    //"isHardcoreModeEnabled",
                    //? else
                    "isHardcore",
            at = @At("RETURN"),
            cancellable = true
    )
    private void hybedwars$forceHardcore(CallbackInfoReturnable<Boolean> cir) {
        if (HardcoreHearts.INSTANCE.shouldForce()) {
            cir.setReturnValue(true);
        }
    }
}
