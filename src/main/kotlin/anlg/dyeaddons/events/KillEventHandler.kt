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
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.Vec3

object KillEventHandler {

    data class TrackedMob(
        val entity: LivingEntity,
        var armorStand: ArmorStand? = null,
        var mobName: String? = null,
        var health: Double? = null,
    )

    data class TrackedArmorStand(
        val entity: ArmorStand,
        var text: String? = null,
        var tracked: Boolean = false
    )

    private val NAME_PATTERN = Regex("""§.([a-z A-Z\-']+) §.([0-9]+(?:[.,0-9]+)?[MKmk]?)§f/""")

    private val livingEntities = mutableMapOf<Int, LivingEntity>()
    private val armorStands = mutableMapOf<Int, TrackedArmorStand>()

    private val trackedMobs = mutableMapOf<Int, TrackedMob>()

    private var tickCounter = 0

    private var killSoundTick: Int? = null

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
    }

    private fun onArmorStandLoad(event: ArmorStandLoadedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val stand = event.entity

        if (stand.isRemoved) return

        armorStands[stand.id] = TrackedArmorStand(entity = stand)
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

        if (killSoundTick != tickCounter) return

        if (SkyblockUtils.getWorldName() == "Private Island") {
            val entity = event.entity

            if (entity.type != EntityType.PLAYER &&
                entity.type != EntityType.ARMOR_STAND
            ) {
                DyeAddons.debug("Killed ${entity.type} at ${entity.position()} tick=$tickCounter")

                EventBus.publish(MobKillEvent(
                    entity as LivingEntity,
                    null,
                    entity.type.description.string,
                    null)
                )
            }
        }

        val tracked = trackedMobs.remove(event.entity.id) ?: return

        DyeAddons.debug(
            "Killed ${tracked.mobName} (${tracked.health} HP) " +
                    "at ${tracked.entity.position()} tick=$tickCounter"
        )
        EventBus.publish(MobKillEvent(tracked.entity, tracked.armorStand, tracked.mobName, tracked.health))
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++
        if (killSoundTick != null && killSoundTick != tickCounter) killSoundTick = null

        if (!SkyblockUtils.isInSkyblock()) return

        for (stand in armorStands.values) {
            if (stand.tracked) continue

            val text = stand.entity.displayName.getFormattedString()

            val match = NAME_PATTERN.find(text) ?: continue

            stand.text = text
            stand.tracked = true

            findMobForArmorStand(stand.entity, match)
        }
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        trackedMobs.clear()
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

    private fun findMobForArmorStand(
        stand: ArmorStand,
        match: MatchResult
    ) {
        val mob = livingEntities.values
            .filter { horizontalDistanceSqr(it.position(), stand.position()) < 16 }
            .minByOrNull { horizontalDistanceSqr(it.position(), stand.position()) }
            ?: return

        trackedMobs[mob.id] = TrackedMob(
            entity = mob,
            armorStand = stand,
            mobName = match.groupValues[1],
            health = parseHealth(match.groupValues[2])
        )
    }

    private fun horizontalDistanceSqr(a: Vec3, b: Vec3): Double {
        val dx = a.x - b.x
        val dz = a.z - b.z
        return dx * dx + dz * dz
    }

}