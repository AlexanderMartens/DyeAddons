package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.data.dungeonMobLevels
import anlg.dyeaddons.events.models.*
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand

object KillEventHandler {

    data class TrackedArmorStand(
        val entity: ArmorStand,
        var mobName: String,
        var health: Double,
    )

    private val NAME_PATTERN = Regex("""§.([a-z A-Z\-']+) §.([0-9]+(?:[.,0-9]+)?[MKmk]?)""")
    private val LEVEL_PATTERN = Regex("""§.\[§.Lv(\d+)§.]""")
    private val DUNGEON_MOB_PATTERN = Regex("""§.([a-z A-Z\-']+) §.""")
    private val HUNTING_PATTERN = Regex("""You caught (?:x\d+|a) ([A-Za-z ]+) Shards?!""")

    private val dungeonMobPrefixes = listOf("Flaming ", "Stormy ", "Speedy ", "Fortified ", "Healthy ", "Healing ")

    private val armorStands = mutableMapOf<Int, TrackedArmorStand>()

    private var tickCounter = 0
    private const val TICK_WINDOW = 3
    private const val KILL_RADIUS = 20

    private var killSoundTick: Int = -1

    fun init() {
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(ArmorStandLoadedEvent::class, ::onArmorStandLoad)
        EventBus.subscribe(ArmorStandCustomNameChangedEvent::class, ::onArmorStandCustomNameChange)
        EventBus.subscribe(ArmorStandDespawnedEvent::class, ::onArmorStandDespawn)
        EventBus.subscribe(EntityDeathEvent::class, ::onEntityDeath)
        EventBus.subscribe(ServerTickEvent::class, ::onServerTick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (event.name != "entity.experience_orb.pickup" || event.pitch != 1.4920635f) return

        killSoundTick = tickCounter
        //DyeAddons.debug("Heard kill sound at tick $tickCounter")
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        HUNTING_PATTERN.matchEntire(event.unformattedText.trim())?.let { matchResult ->
            val mobName = matchResult.groups[1]!!.value
            DyeAddons.debug("Killed $mobName", DebugCategories.KILL_EVENT)
            EventBus.publish(MobKillEvent(mobName, 0.0, 0, null))
        }
    }

    private fun onArmorStandLoad(event: ArmorStandLoadedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val stand = event.armorStand

        if (stand.isRemoved) return

        armorStands[stand.id] = TrackedArmorStand(stand, stand.displayName.string, stand.health.toDouble())
    }

    private fun onArmorStandCustomNameChange(event: ArmorStandCustomNameChangedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val armorStand = armorStands[event.entityId] ?: return

        val name = event.customName.getFormattedString()
        val match = NAME_PATTERN.find(name) ?: return
        val health = parseHealth(match.groupValues[2]) ?: return

        armorStand.health = health
        armorStand.mobName = match.groupValues[1]

        if (health <= 0 && tickCounter in (killSoundTick)..(killSoundTick + TICK_WINDOW)) {
            onMobKilled(armorStand)
            armorStands.remove(event.entityId)
            return
        }
    }

    private fun onArmorStandDespawn(event: ArmorStandDespawnedEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (tickCounter in (killSoundTick)..(killSoundTick + TICK_WINDOW)) {
            val armorStand = armorStands[event.armorStand.id]
            if (armorStand != null) onMobKilled(armorStand)
        }

        armorStands.remove(event.armorStand.id)
    }

    private fun onEntityDeath(event: EntityDeathEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        if (SkyblockUtils.getWorldName() != "Private Island") return

        if (killSoundTick != tickCounter) return

        val entity = event.entity

        if (entity !is LivingEntity) return

        DyeAddons.debug("Killed ${entity.type.description.string} (${entity.maxHealth.toDouble()} HP)", DebugCategories.KILL_EVENT)

        EventBus.publish(
            MobKillEvent(
                mobName = entity.type.description.string,
                health = entity.maxHealth.toDouble(),
                level = 0,
                armorStand = null
            )
        )
    }

    private fun onServerTick(event: ServerTickEvent) {
        tickCounter = event.tick
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        armorStands.clear()
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
        if (stand.mobName == "Armor Stand" ||
            stand.entity.distanceTo(mc.player!!) > KILL_RADIUS) return

        // Remove any prefixes on dungeon mobs
        val mobName = if (TabListUtils.getLineAfter("Dungeon:").trim() == "Catacombs") {
            dungeonMobPrefixes.firstOrNull { stand.mobName.startsWith(it) }
                ?.let { stand.mobName.removePrefix(it) } ?: stand.mobName
        } else stand.mobName

        val nameTag = stand.entity.displayName.getFormattedString()
        val match = LEVEL_PATTERN.find(nameTag) ?: DUNGEON_MOB_PATTERN.find(nameTag)
        val level = match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: getDungeonMobLevel(mobName)

        DyeAddons.debug("Killed $mobName [Lv$level] (${stand.health} HP) at tick $tickCounter, " +
                "difference is ${tickCounter - killSoundTick}, " +
                "distance: ${stand.entity.distanceTo(mc.player!!)}",
            DebugCategories.KILL_EVENT)

        EventBus.publish(
            MobKillEvent(
                mobName = mobName,
                health = stand.health,
                level = level,
                armorStand = stand.entity
            )
        )
    }

    private fun getDungeonMobLevel(mobName: String): Int {
        val floor = ScoreboardUtils.getLineAfter("The Catacombs").removePrefix("(").removeSuffix(")")
        if (floor.isBlank()) return 1
        return dungeonMobLevels[mobName]?.get(floor) ?: 1
    }

}