package tomeko.hybedwars.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.hybedwars.coloredbeds.ColoredBeds;

@Mixin(TextureMap.class)
abstract class TextureMapMixin {
    @Shadow
    public abstract TextureAtlasSprite registerSprite(ResourceLocation location);

    @Inject(method = "loadSprites", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/IIconCreator;registerSprites(Lnet/minecraft/client/renderer/texture/TextureMap;)V", shift = At.Shift.AFTER))
    private void hybedwars$registerBedSprites(IResourceManager resourceManager, IIconCreator iconCreator, CallbackInfo ci) {
        for (String color : ColoredBeds.COLORS_REVERSE.values()) {
            registerSprite(new ResourceLocation(ColoredBeds.COLORED_BED + color));
        }
    }
}
*///?}
