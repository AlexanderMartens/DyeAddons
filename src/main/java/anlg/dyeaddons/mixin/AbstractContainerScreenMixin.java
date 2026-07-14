package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.SlotClickEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void dyeaddons$slotClicked(Slot slot, int slotId, int button, ContainerInput clickType, CallbackInfo ci) {
        EventBus.INSTANCE.publish(
                new SlotClickEvent((AbstractContainerScreen<?>)(Object)this, slot, slotId, button, clickType));
    }
}
