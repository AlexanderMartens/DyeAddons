package anlg.dyeaddons.events.models

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu


/**
 * Event fired when a Hypixel custom inventory opens.
 * @param screen The container screen that was opened.
 */
data class InventoryOpenEvent(val screen: AbstractContainerScreen<*>) {

    val menu: AbstractContainerMenu get() = screen.menu
    val inventoryId: Int get() = menu.containerId
    val inventoryName: String get() = screen.getTitle().string

    val slots = menu.slots.filter { !it.item.isEmpty && it.container !is Inventory }

}
