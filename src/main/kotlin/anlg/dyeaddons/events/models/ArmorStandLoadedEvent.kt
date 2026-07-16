package anlg.dyeaddons.events.models

import net.minecraft.world.entity.decoration.ArmorStand

/**
 * Event for when an ArmorStand is loaded into the client world.
 * @param armorStand The ArmorStand that was loaded.
 */
data class ArmorStandLoadedEvent(
    val armorStand: ArmorStand
)
