package tomeko.hybedwars.mixins;

//? if 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.hybedwars.event.ClientReceiveMessageEvents;

@Mixin(NetHandlerPlayClient.class)
abstract class NetHandlerPlayClientMixin {
    @WrapOperation(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V"))
    private void hybedwars$onChat(GuiNewChat chat, IChatComponent message, Operation<Void> original) {
        message = hybedwars$process(message, false);
        if (message != null) original.call(chat, message);
    }

    private static IChatComponent hybedwars$process(IChatComponent message, boolean overlay) {
        boolean allowed = ClientReceiveMessageEvents.ALLOW_GAME.invoker().allowReceiveGameMessage(message, overlay);
        if (!overlay) {
            allowed &= ClientReceiveMessageEvents.ALLOW_CHAT.invoker().allowReceiveChatMessage(message);
        }
        if (!allowed) {
            return null;
        }

        message = ClientReceiveMessageEvents.MODIFY_GAME.invoker().modifyReceivedGameMessage(message, overlay);
        if (message == null) {
            return null;
        }
        if (!overlay) {
            message = ClientReceiveMessageEvents.MODIFY_CHAT.invoker().modifyReceivedChatMessage(message);
            if (message == null) {
                return null;
            }
        }

        ClientReceiveMessageEvents.GAME.invoker().onReceiveGameMessage(message, overlay);
        if (!overlay) {
            ClientReceiveMessageEvents.CHAT.invoker().onReceiveChatMessage(message);
        }
        return message;
    }
}
*///?}