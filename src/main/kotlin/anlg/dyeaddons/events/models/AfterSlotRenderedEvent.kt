package anlg.dyeaddons.events.models

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

/**
 * Published after a slot is drawn in an abstract container.
 * Fired at the end of renderSlot, after the slot background and item are rendered,
 * so listeners can draw on top of the item.
 */
data class AfterSlotRenderedEvent(
    val drawContext: GuiGraphicsExtractor,
    val slot: Slot,
    val screen: AbstractContainerScreen<*>
)
