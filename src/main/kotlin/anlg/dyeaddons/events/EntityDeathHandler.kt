package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.EntityDeathEvent
import net.minecraft.world.entity.LivingEntity

object EntityDeathHandler {

    fun init() {
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private val deadEntities = mutableSetOf<Int>()

    fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        val level = mc.level ?: return

        for (entity in level.entitiesForRendering()) {
            if (entity is LivingEntity &&
                entity.isDeadOrDying &&
                deadEntities.add(entity.id)
            ) {
                EventBus.publish(EntityDeathEvent(entity))
            }
        }
    }
}