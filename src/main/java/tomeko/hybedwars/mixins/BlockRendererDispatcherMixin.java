package tomeko.hybedwars.mixins;

//? if 1.8.9 {
/*import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.hybedwars.coloredbeds.ColoredBeds;

@Mixin(BlockRendererDispatcher.class)
abstract class BlockRendererDispatcherMixin {
    @Inject(method = "getModelFromBlockState", at = @At("RETURN"), cancellable = true)
    private void hybedwars$coloredBeds(IBlockState state, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<IBakedModel> cir) {
        IBakedModel model = ColoredBeds.getBedModel(state, pos, cir.getReturnValue());
        if (model != null) {
            cir.setReturnValue(model);
        }
    }
}
*///?}
