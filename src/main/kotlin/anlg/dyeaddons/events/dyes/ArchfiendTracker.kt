package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

enum class ArchfiendDice(val baseChance : Float) {
    ARCHFIEND(6_666f),
    HIGH_CLASS_ARCHFIEND(666f)
}
object ArchfiendTracker {

    private val ARCHFIEND_PATTERN = Regex("""§eYour §5Archfiend Dice §erolled a""")

    private val HIGH_CLASS_PATTERN = Regex("""§eYour §6High Class Archfiend Dice §erolled a""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {

        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (ARCHFIEND_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats(ArchfiendDice.ARCHFIEND)
            updateDyeProgress(ArchfiendDice.ARCHFIEND)
        }
        if (HIGH_CLASS_PATTERN.containsMatchIn(event.formattedText)) {
            updateDyeStats(ArchfiendDice.HIGH_CLASS_ARCHFIEND)
            updateDyeProgress(ArchfiendDice.HIGH_CLASS_ARCHFIEND)
        }
    }

    private fun updateDyeStats(dice : ArchfiendDice) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.ARCHFIEND]?.statistics ?: return

        DyeAddons.debug("Tracked $dice dice roll", DebugCategories.DYE_PROGRESS_EVENT)
        when (dice) {
            ArchfiendDice.ARCHFIEND -> stats.incrementInt("Archfiend Dice Rolls")
            ArchfiendDice.HIGH_CLASS_ARCHFIEND -> stats.incrementInt("High Class Archfiend Dice Rolls")
        }
    }

    private fun updateDyeProgress(dice : ArchfiendDice) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.ARCHFIEND) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.ARCHFIEND]?.progress += (1.0 / dice.baseChance.toDouble()) * multiplier
    }

}