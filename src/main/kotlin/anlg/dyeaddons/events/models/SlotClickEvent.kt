package anlg.dyeaddons.events.models

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot

data class SlotClickEvent(
    val container: AbstractContainerScreen<*>,
    val slot: Slot?,
    val slotId: Int,
    val button: Int,
    val clickType: ContainerInput
) {
    val item = container.menu.items.takeIf { it.size > slotId && slotId >= 0 }?.get(slotId)
}