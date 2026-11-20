package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.AfterSlotRenderedEvent;
import anlg.dyeaddons.events.models.BeforeSlotRenderedEvent;
import anlg.dyeaddons.events.models.SlotClickEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void dyeaddons$slotClicked(Slot slot,
                                       int slotId,
                                       //?if >=26.3 {
                                       MouseButtonEvent event,
                                       //?} else {
                                       /*int button,
                                       *///?}
                                       ContainerInput containerInput,
                                       CallbackInfo ci) {
        EventBus.INSTANCE.publish(
                //?if >=26.3 {
                new SlotClickEvent((AbstractContainerScreen<?>)(Object)this, slot, slotId, event.button(), containerInput));
                //?} else {
                /*new SlotClickEvent((AbstractContainerScreen<?>)(Object)this, slot, slotId, button, containerInput));
                *///?}
    }

    @Inject(method = "extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;item(Lnet/minecraft/world/item/ItemStack;III)V"), require = 1)
    private void dyeaddons$onSlotBeforeItemDrawn(GuiGraphicsExtractor context, Slot slot, int x, int y, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        EventBus.INSTANCE.publish(new BeforeSlotRenderedEvent(context, slot, screen));
    }

    @Inject(method = "extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"), require = 1)
    private void dyeaddons$onSlotAfterItemDrawn(GuiGraphicsExtractor context, Slot slot, int x, int y, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        EventBus.INSTANCE.publish(new AfterSlotRenderedEvent(context, slot, screen));
    }
}
