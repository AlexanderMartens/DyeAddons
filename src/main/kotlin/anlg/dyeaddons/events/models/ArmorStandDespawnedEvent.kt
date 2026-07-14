package anlg.dyeaddons.events.models

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.item.Items

/**
 * Event for when an Armor Stand is removed from the world (despawned/unloaded).
 * @param armorStand The ArmorStand that was removed.
 */
data class ArmorStandDespawnedEvent(
    val armorStand: ArmorStand
) {
    private val head = armorStand.getItemBySlot(EquipmentSlot.HEAD)
    val armorStandName = if (head.`is`(Items.PLAYER_HEAD)) head.displayName.string.removePrefix("[").removeSuffix("]") else null
}
