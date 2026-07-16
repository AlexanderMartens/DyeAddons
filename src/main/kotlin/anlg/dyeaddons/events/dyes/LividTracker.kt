package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import anlg.dyeaddons.utils.calc.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

object LividTracker {

    private val DUNGEON_FLOOR_PATTERN = Regex("""(Master Mode )?The Catacombs - Floor ([A-Z]+)""")

    private val DUNGEON_SCORE_PATTERN = Regex("""Team Score: (\d+) \([A-Z]\+?\)(?: \(NEW RECORD!\))?""")

    private var completedM5 = false

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (DUNGEON_FLOOR_PATTERN.matches(event.unformattedText.trim())) {
            val match = DUNGEON_FLOOR_PATTERN.find(event.unformattedText.trim())
            val masterMode = match?.groupValues?.get(1) ?: ""
            val floor = match?.groupValues?.get(2) ?: ""
            completedM5 = masterMode == "Master Mode " && floor == "V"
        }
        if (DUNGEON_SCORE_PATTERN.matches(event.unformattedText.trim())) {
            val score = DUNGEON_SCORE_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1)?.toInt() ?: 0

            val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["m5"]
            val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
            val addedMeter = (1f + pityShard / 100f) * (if (score >= 270) (score * (if (score < 300) 0.7f else 1f)) else 0f)
            meter?.progress += addedMeter.toInt()

            if (score >= 300 && completedM5) {
                updateDyeStats()
                updateDyeProgress()
            }
            completedM5 = false
        }
        // TODO: Track Kismets on bedrock chests
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.LIVID]?.statistics ?: return

        stats.incrementInt("Master Mode Floor 5 S+ Completions")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.LIVID) ?: 1

        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["m5"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.M5.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.LIVID]?.progress += (1.0 / 5_000.0) * meterMultiplier * multiplier
    }

}