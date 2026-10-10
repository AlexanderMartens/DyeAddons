package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.SlotClickEvent;
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
}
