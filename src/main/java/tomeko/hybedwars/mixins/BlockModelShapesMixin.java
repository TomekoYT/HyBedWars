package tomeko.hybedwars.mixins;

//? if 1.8.9 {
/*import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.hybedwars.coloredbeds.ColoredBeds;

@Mixin(BlockModelShapes.class)
abstract class BlockModelShapesMixin {
    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void hybedwars$coloredBedParticles(IBlockState state, CallbackInfoReturnable<TextureAtlasSprite> cir) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        IBakedModel model = ColoredBeds.getBedModel(state, Minecraft.getMinecraft().thePlayer.getPosition(), null);
        if (model != null) {
            cir.setReturnValue(model.getParticleTexture());
        }
    }

    @Inject(method = "reloadModels", at = @At("HEAD"))
    private void hybedwars$reloadModels(CallbackInfo ci) {
        ColoredBeds.reloadModels();
    }
}
*///?}
