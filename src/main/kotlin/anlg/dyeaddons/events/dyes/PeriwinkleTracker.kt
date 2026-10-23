package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.dungeonMobLevels
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils

object PeriwinkleTracker {

    private val dye = Dye.PERIWINKLE

    private val RUNIC_LEVEL_PATTERN = Regex("""§5\[§dLv(\d+)§5]""")
    private val RUNIC_DUNGEON_MOB_PATTERN = Regex("""§5([a-z A-Z\-']+) §d""")

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() == "The Rift") return

        if (event.armorStand == null) return

        val nameTag = event.armorStand.displayName.getFormattedString()
        val match = RUNIC_LEVEL_PATTERN.find(nameTag) ?: RUNIC_DUNGEON_MOB_PATTERN.find(nameTag) ?: return
        val level = match.groupValues.getOrNull(1)?.toIntOrNull() ?: getDungeonMobLevel(event.mobName)

        DyeAddons.debug("Tracked Runic Kill, level: $level", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(level)
        updateDyeProgress(level)
    }

    private fun updateDyeStats(level: Int) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (level) {
            in 1..49 -> stats.incrementStat(dye, "T1 Runic Kills")
            in 50..99 -> stats.incrementStat(dye, "T2 Runic Kills")
            in 100..149 -> stats.incrementStat(dye, "T3 Runic Kills")
            in 150..199 -> stats.incrementStat(dye, "T4 Runic Kills")
            in 200..Int.MAX_VALUE -> stats.incrementStat(dye, "T5 Runic Kills")
            else -> return
        }
    }

    private fun updateDyeProgress(level: Int) {
        val dropChance = when (level) {
            in 1..49 -> dye.getDropRate("T1")
            in 50..99 -> dye.getDropRate("T2")
            in 100..149 -> dye.getDropRate("T3")
            in 150..199 -> dye.getDropRate("T4")
            in 200..Int.MAX_VALUE -> dye.getDropRate("T5")
            else -> return
        }

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / dropChance * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            dropChance,
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }

    private fun getDungeonMobLevel(mobName: String): Int {
        val floor = ScoreboardUtils.getLineAfter("The Catacombs").removePrefix("(").removeSuffix(")")
        if (floor.isBlank()) return 1
        return dungeonMobLevels[mobName]?.get(floor) ?: 1
    }
}