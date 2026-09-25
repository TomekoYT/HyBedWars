package tomeko.hybedwars.mixins;

//? if ornithe {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.hybedwars.event.ClientReceiveMessageEvents;

@Mixin(NetHandlerPlayClient.class)
public abstract class NetHandlerPlayClientMixin {
    @Inject(
            method = "handleChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void hybedwars$onHandleChat(S02PacketChat packet, CallbackInfo ci) {
        boolean overlay = packet.getType() == 2;

        IChatComponent message = hybedwars$process(packet.getChatComponent(), overlay);

        ci.cancel();

        if (message == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (overlay) {
            mc.ingameGUI.setRecordPlaying(message, false);
        } else {
            mc.ingameGUI.getChatGUI().printChatMessage(message);
        }
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