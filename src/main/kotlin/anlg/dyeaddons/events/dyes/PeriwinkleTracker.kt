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
        updateDyeStats()
        updateDyeProgress(level)
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        stats.incrementStat(dye, "Runic Kills")
    }

    private fun updateDyeProgress(level: Int) {
        val dropChance = when (level) {
            in 1..99 -> 500_000
            in 100..199 -> 400_000
            in 200..299 -> 300_000
            in 300..399 -> 200_000
            in 400..Int.MAX_VALUE -> 100_000
            else -> return
        }

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / dropChance * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.MAGIC_FIND,
            DyeMultiplier.LOOTING,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE,
            DyeMultiplier.PAINT_PALETTE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            dropChance.toDouble(),
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }

    private fun getDungeonMobLevel(mobName: String): Int {
        val floor = ScoreboardUtils.getLineAfter("The Catacombs").removePrefix("(").removeSuffix(")")
        if (floor.isBlank()) return 1
        return dungeonMobLevels[mobName]?.get(floor) ?: 1
    }
}