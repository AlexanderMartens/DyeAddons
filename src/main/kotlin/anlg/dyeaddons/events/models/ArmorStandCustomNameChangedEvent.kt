package anlg.dyeaddons.events.models

import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3

data class ArmorStandCustomNameChangedEvent(
    val entityId: Int,
    val isFirstLoaded: Boolean,
    val previousCustomName: Component,
    val customName: Component,
    val position: Vec3
)
