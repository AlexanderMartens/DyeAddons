package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils

enum class ArchfiendDice(val baseChance : Double) {
    ARCHFIEND(6_666.0),
    HIGH_CLASS_ARCHFIEND(666.0)
}
object ArchfiendTracker {

    private val dye = Dye.ARCHFIEND

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
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        DyeAddons.debug("Tracked $dice dice roll", DebugCategories.DYE_PROGRESS_EVENT)
        when (dice) {
            ArchfiendDice.ARCHFIEND -> stats.incrementStat(dye, "Archfiend Dice Rolls")
            ArchfiendDice.HIGH_CLASS_ARCHFIEND -> stats.incrementStat(dye, "High Class Archfiend Dice Rolls")
        }
    }

    private fun updateDyeProgress(dice : ArchfiendDice) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / dice.baseChance) * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            dice.baseChance,
            dropRate)
    }

}