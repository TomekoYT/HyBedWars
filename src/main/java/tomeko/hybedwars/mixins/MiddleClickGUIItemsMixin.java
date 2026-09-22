package tomeko.hybedwars.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

//? if 1.8.9 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
*///?} else {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >= 26.3 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
//?}
import tomeko.hybedwars.config.HyBedWarsConfig;
import tomeko.hybedwars.location.HypixelPackets;

//? if 1.8.9 {
//@Mixin(GuiContainer.class)
//?} else {
@Mixin(AbstractContainerScreen.class)
//?}
public abstract class MiddleClickGUIItemsMixin {
    @WrapOperation(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target =
                            //? if 1.8.9 {
                            //"Lnet/minecraft/client/gui/inventory/GuiContainer;handleMouseClick(Lnet/minecraft/inventory/Slot;III)V"
                            //?} elif >= 26.3 {
                            //"Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;ILnet/minecraft/client/input/MouseButtonEvent;Lnet/minecraft/world/inventory/ContainerInput;)V"
                            //?} else {
                            "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V"
                    //?}
            )
    )
    private void hybedwars$useMiddleClick(
            //? if 1.8.9 {
            //GuiContainer instance,
            //?} else {
            AbstractContainerScreen instance,
            //?}
            Slot slot,
            int slotId,
            //? if >= 26.3 {
            //MouseButtonEvent event,
            //?} else {
            int clickedButton,
            //?}
            //? if 1.8.9 {
            //int clickType,
            //?} else {
            ContainerInput clickType,
            //?}
            Operation<Void> original
    ) {
        //? if >= 26.3 {
        /*int clickedButton = switch (event.button()) {
            case 1 -> 0;
            case 3 -> 1;
            default -> event.button();
        };
        *///?}

        if (hybedwars$shouldCallOriginal(instance, slot, clickedButton, clickType)) {
            original.call(
                    instance,
                    slot,
                    slotId,
                    //? if >= 26.3 {
                    //event,
                    //?} else {
                    clickedButton,
                    //?}
                    clickType
            );
            return;
        }

        original.call(
                instance,
                slot,
                slotId,
                //? if >= 26.3 {
                //new MouseButtonEvent(event.x(), event.y(), new MouseButtonInfo(2, event.modifiers())),
                //?} else {
                2,
                //?}
                //? if 1.8.9 {
                //3
                //?} elif >= 26.1 {
                ContainerInput.CLONE
                //?} else {
                //ClickType.CLONE
                //?}
        );
    }


    private static boolean hybedwars$shouldCallOriginal(
            //? if 1.8.9 {
            //GuiContainer instance,
            //?} else {
            AbstractContainerScreen instance,
            //?}
            Slot slot,
            int clickedButton,
            //? if 1.8.9 {
            //int clickType
            //?} else {
            ContainerInput clickType
            //?}
    ) {
        if (
                clickedButton != 0
                        //? if 1.8.9 {
                        //|| clickType != 0
                        //?} else {
                        || clickType != ContainerInput.PICKUP
                        //?}
                        //? if 1.8.9 {
                        //|| !(instance instanceof GuiChest)
                        //?} else {
                        || !(instance.getMenu() instanceof ChestMenu)
                        //?}
                        || !HypixelPackets.INSTANCE.getOnHypixel()
                        || slot == null
            //? if 1.8.9 {
                        /*|| !slot.getHasStack()
                        || !(instance.inventorySlots instanceof ContainerChest)
                        *///?}
        ) return true;

        //? if 1.8.9 {
        //List<String> tooltip = slot.getStack().getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        //?} else {
        List<Component> tooltip = slot.getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        //?}
        for (
            //? if 1.8.9 {
            //String line
            //?} else {
                Component line
            //?}
                : tooltip
        ) {
            if (hybedwars$moreThanOneButton(
                    line
                            //? if fabric {
                            .getString()
                    //?}
            )) return true;
        }

        String containerTitle =
                //? if 1.8.9 {
                //((ContainerChest) instance.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
                //?} else {
                instance.getTitle().getString();
        //?}

        return !HyBedWarsConfig.INSTANCE.getMiddleClickInBedwarsShop()
                || !HypixelPackets.INSTANCE.getInBedwars()
                || (!containerTitle.equals("Quick Buy")
                && !containerTitle.equals("Blocks")
                && !containerTitle.equals("Melee")
                && !containerTitle.equals("Armor")
                && !containerTitle.equals("Tools")
                && !containerTitle.equals("Ranged")
                && !containerTitle.equals("Potions")
                && !containerTitle.equals("Utility")
                && !containerTitle.equals("Rotating Items")
                && !containerTitle.equals("Upgrades & Traps"));
    }

    private static boolean hybedwars$moreThanOneButton(String text) {
        text = text.toLowerCase();

        return text.contains("right-click")
                || text.contains("right click")
                || text.contains("left-click")
                || text.contains("left click");
    }
}
