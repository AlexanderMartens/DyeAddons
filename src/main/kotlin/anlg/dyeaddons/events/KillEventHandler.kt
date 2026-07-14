package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.events.models.ArmorStandDespawnedEvent
import anlg.dyeaddons.events.models.ArmorStandLoadedEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.EntityDeathEvent
import anlg.dyeaddons.events.models.EntityDespawnEvent
import anlg.dyeaddons.events.models.EntitySpawnEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

object KillEventHandler {

    data class TrackedArmorStand(
        val entity: ArmorStand,
        var mobName: String,
        var health: Double,
    )

    private val NAME_PATTERN = Regex("""§.([a-z A-Z\-']+) §.([0-9]+(?:[.,0-9]+)?[MKmk]?)§f/""")

    private val livingEntities = mutableMapOf<Int, LivingEntity>()
    private val armorStands = mutableMapOf<Int, TrackedArmorStand>()

    private var tickCounter = 0
    private val tickWindow = 2

    private var killSoundTick: Int = -1

    fun init() {
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
        EventBus.subscribe(ArmorStandLoadedEvent::class, ::onArmorStandLoad)
        EventBus.subscribe(ArmorStandDespawnedEvent::class, ::onArmorStandDespawn)
        EventBus.subscribe(EntitySpawnEvent::class, ::onEntitySpawn)
        EventBus.subscribe(EntityDespawnEvent::class, ::onEntityDespawn)
        EventBus.subscribe(EntityDeathEvent::class, ::onEntityDeath)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (event.name != "entity.experience_orb.pickup" || event.pitch != 1.4920635f) return

        killSoundTick = tickCounter
        //DyeAddons.debug("Heard kill sound at tick $tickCounter")
    }

    private fun onArmorStandLoad(event: ArmorStandLoadedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val stand = event.entity

        if (stand.isRemoved) return

        armorStands[stand.id] = TrackedArmorStand(stand, stand.displayName.string, stand.health.toDouble())
    }

    private fun onArmorStandDespawn(event: ArmorStandDespawnedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        armorStands.remove(event.armorStand.id)
    }

    private fun onEntitySpawn(event: EntitySpawnEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val entity = event.entity
        if (entity !is LivingEntity || entity is ArmorStand || entity.isRemoved) return

        livingEntities[entity.id] = entity
    }

    private fun onEntityDespawn(event: EntityDespawnEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        livingEntities.remove(event.entity.id)
    }

    private fun onEntityDeath(event: EntityDeathEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (SkyblockUtils.getWorldName() != "Private Island") return

        if (killSoundTick != tickCounter) return

        val entity = event.entity

        if (entity !is LivingEntity) return

        DyeAddons.debug("Killed ${entity.type.description.string} (${entity.maxHealth.toDouble()} HP)")

        EventBus.publish(
            MobKillEvent(
                mobName = entity.type.description.string,
                health = entity.maxHealth.toDouble(),
                armorStand = null
            )
        )
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++

        if (!SkyblockUtils.isInSkyblock()) return

        val iterator = armorStands.iterator()

        while (iterator.hasNext()) {
            val (_, stand) = iterator.next()

            val text = stand.entity.displayName.getFormattedString()

            val match = NAME_PATTERN.find(text) ?: continue

            val health = parseHealth(match.groupValues[2]) ?: continue

            if (health <= 0 && stand.health > 0 && tickCounter in (killSoundTick)..(killSoundTick + tickWindow)) {
                onMobKilled(stand)
                iterator.remove()
                continue
            }

            stand.health = health
            stand.mobName = match.groupValues[1]
        }
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        armorStands.clear()
        livingEntities.clear()
    }

    private fun parseHealth(raw: String?): Double? {
        raw ?: return null
        val clean = raw.replace(",", "")
        return when {
            clean.endsWith("M") || clean.endsWith("m") -> clean.dropLast(1).toDoubleOrNull()?.times(1_000_000)
            clean.endsWith("K") || clean.endsWith("k") -> clean.dropLast(1).toDoubleOrNull()?.times(1_000)
            else -> clean.toDoubleOrNull()
        }
    }

    private fun onMobKilled(
        stand: TrackedArmorStand
    ) {
        DyeAddons.debug(
            "Killed ${stand.mobName} (${stand.health} HP) at tick $tickCounter"
        )

        EventBus.publish(
            MobKillEvent(
                mobName = stand.mobName,
                health = stand.health,
                armorStand = stand.entity
            )
        )
    }

}