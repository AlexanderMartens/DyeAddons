package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object PeriwinkleTracker {

    private val LEVEL_PATTERN = Regex("""§.\[§.Lv(\d+)§.]""")

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() == "The Rift") return

        if (event.armorStand == null) return
        if (!event.armorStand.displayName.getFormattedString().trim().startsWith("§5")) return

        val level = LEVEL_PATTERN.find(event.armorStand.displayName.getFormattedString())?.groupValues?.get(1)?.toIntOrNull() ?: return

        DyeAddons.debug("Tracked Runic Kill, level: $level", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats()
        updateDyeProgress(level)
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PERIWINKLE]?.statistics ?: return

        stats.incrementInt("Runic Kills")
    }

    private fun updateDyeProgress(level: Int) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.PERIWINKLE) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PERIWINKLE]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropChance = when (level) {
            in 1..99 -> 500_000
            in 100..199 -> 400_000
            in 200..299 -> 300_000
            in 300..399 -> 200_000
            in 400..Int.MAX_VALUE -> 100_000
            else -> return
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PERIWINKLE]?.progress += (1.0 / dropChance) * (1.0 + magicFind / 100.0) * (1.0 + looting * 0.15) * multiplier
    }

}