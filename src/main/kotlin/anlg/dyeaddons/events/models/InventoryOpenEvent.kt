package anlg.dyeaddons.events.models

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

/**
 * Event fired when a Hypixel custom inventory opens.
 * @param screen The container screen that was opened.
 */
data class InventoryOpenEvent(val screen: AbstractContainerScreen<*>)
