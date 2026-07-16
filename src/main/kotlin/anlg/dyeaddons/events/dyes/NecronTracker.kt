package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.ChestType
import anlg.dyeaddons.events.models.InstanceType
import anlg.dyeaddons.events.models.KismetUsedEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import anlg.dyeaddons.utils.calc.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

object NecronTracker {

    private val DUNGEON_FLOOR_PATTERN = Regex("""(Master Mode )?The Catacombs - Floor ([A-Z]+)""")

    private val DUNGEON_SCORE_PATTERN = Regex("""Team Score: (\d+) \([A-Z]\+?\)(?: \(NEW RECORD!\))?""")

    private var completedM7 = false

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(KismetUsedEvent::class, ::onKismetUsed)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (DUNGEON_FLOOR_PATTERN.matches(event.unformattedText.trim())) {
            val match = DUNGEON_FLOOR_PATTERN.find(event.unformattedText.trim())
            val masterMode = match?.groupValues?.get(1) ?: ""
            val floor = match?.groupValues?.get(2) ?: ""
            completedM7 = masterMode == "Master Mode " && floor == "VII"
        }
        if (DUNGEON_SCORE_PATTERN.matches(event.unformattedText.trim())) {
            val score = DUNGEON_SCORE_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1)?.toInt() ?: 0

            val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["m7"]
            val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
            val addedMeter = (1f + pityShard / 100f) * (if (score >= 270) (score * (if (score < 300) 0.7f else 1f)) else 0f)
            meter?.progress += addedMeter.toInt()

            if (score >= 270 && completedM7) { // Assuming S comp means you get bedrock chest
                updateDyeStats()
                updateDyeProgress()
                DyeAddons.debug("Tracked M7 completion")
            }
            completedM7 = false
        }
    }

    private fun onKismetUsed(event: KismetUsedEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (event.chestType != ChestType.BEDROCK ||
            event.instanceType != InstanceType.MASTER_CATACOMBS_FLOOR_VII) return

        updateDyeStats()
        updateDyeProgress()
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NECRON]?.statistics ?: return

        stats.incrementInt("Master Mode Floor 7 Completions")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.NECRON) ?: 1

        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["m7"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.M7.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NECRON]?.progress += (1.0 / 2_500.0) * meterMultiplier * multiplier
    }

}