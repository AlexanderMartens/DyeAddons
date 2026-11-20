package anlg.dyeaddons.events.models

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot

/**
 * Published when a slot is about to be drawn in an abstract container.
 * Fired at the start of renderSlot, before the slot background and item are rendered,
 * so listeners can draw under the item.
 */
data class BeforeSlotRenderedEvent (
    val drawContext: GuiGraphicsExtractor,
    val slot: Slot,
    val screen: AbstractContainerScreen<*>
)
