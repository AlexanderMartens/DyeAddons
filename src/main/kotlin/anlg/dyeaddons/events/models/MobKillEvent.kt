package anlg.dyeaddons.events.models

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

/**
 * Triggers when the player kills a mob. Includes the entity and armorstand containing the mob data.
 */
data class MobKillEvent (
    val entity: LivingEntity,
    val armorStand: ArmorStand?,
    val mobName: String?,
    val health: Double?
)