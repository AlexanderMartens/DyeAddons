package anlg.dyeaddons.events.models

import net.minecraft.world.entity.item.ItemEntity

/**
 * Event for when an ItemEntity is unloading from the client world.
 * @param itemEntity The ItemEntity that was unloaded.
 */
data class ItemEntityDespawnEvent(
    val itemEntity: ItemEntity
)
