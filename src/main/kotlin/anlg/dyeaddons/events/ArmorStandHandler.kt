package anlg.dyeaddons.events

import anlg.dyeaddons.events.models.ArmorStandCustomNameChangedEvent
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.decoration.ArmorStand

/**
 * Credits: Feesh
 */
object ArmorStandHandler {

    fun init() {

    }

    @JvmStatic
    fun onArmorStandCustomNameChanged(armorStand: ArmorStand, previousCustomName: Component?) {
        if (!SkyblockUtils.isInSkyblock()) return
        if (!armorStand.isAlive) return

        val name = armorStand.customName ?: return
        if (name.string.isEmpty()) return

        val previousUnformatted = previousCustomName?.string ?: ""
        val isFirstLoaded = previousCustomName == null || previousUnformatted.isEmpty()

        EventBus.publish(ArmorStandCustomNameChangedEvent(
            entityId = armorStand.id,
            isFirstLoaded = isFirstLoaded,
            previousCustomName = previousCustomName ?: Component.literal(""),
            customName = name,
            position = armorStand.position()
        ))
    }
}