package anlg.dyeaddons.events.models

import net.minecraft.world.entity.decoration.ArmorStand

/**
 * Triggers when the player kills a mob. ArmorStand is null if there is no armorstand.
 */
data class MobKillEvent(
    val mobName: String,
    val health: Double,
    val armorStand: ArmorStand?
)