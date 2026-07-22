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

enum class Pest(val baseChance: Int){
    NORMAL(250_000),
    ELUSIVE(50_000)
}

object DungTracker {

    private val PEST_PATTERN = Regex("""You received \d+x Enchanted .+ for killing (?:a|an) (.+)!""")

    private var lunarMothFix = 0 // Lunar moth gives 3 rewards, so count every 3 rewards

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        if (PEST_PATTERN.matches(event.unformattedText.trim())) {
            val pest = PEST_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1) ?: return
            if (pest == "Field Mouse") {
                updateDyeStats(Pest.ELUSIVE)
                updateDyeProgress(Pest.ELUSIVE)
                DyeAddons.debug("Tracked $pest pest", DebugCategories.DYE_PROGRESS_EVENT)
            } else if (pest == "Lunar Moth") {
                if (lunarMothFix == 0) {
                    updateDyeStats(Pest.ELUSIVE)
                    updateDyeProgress(Pest.ELUSIVE)
                    DyeAddons.debug("Tracked $pest pest", DebugCategories.DYE_PROGRESS_EVENT)
                }
                lunarMothFix = (lunarMothFix + 1) % 3
            } else {
                updateDyeStats(Pest.NORMAL)
                updateDyeProgress(Pest.NORMAL)
                DyeAddons.debug("Tracked $pest pest", DebugCategories.DYE_PROGRESS_EVENT)
            }
        }

    }

    private fun updateDyeStats(pest: Pest) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.DUNG]?.statistics ?: return

        when (pest) {
            Pest.NORMAL -> stats.incrementInt("Pest Kills")
            Pest.ELUSIVE -> stats.incrementInt("Elusive Pest Kills")
        }
    }

    private fun updateDyeProgress(pest: Pest) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.DUNG) ?: 1

        val overbloom = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.DUNG]?.statistics["Overbloom"]?.asFloat() ?: 0f

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.DUNG]?.progress += (1.0 / pest.baseChance) * (1.0 + overbloom/100.0) * multiplier
    }

}