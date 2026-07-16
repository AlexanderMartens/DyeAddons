package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

object SangriaTracker {

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")


    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Riftstalker Bloodfiend")) {
                "I" -> 1
                "II" -> 2
                "III" -> 3
                "IV" -> 4
                "V" -> 5
                else -> 0
            }
            updateDyeStats(tier)
            updateDyeProgress(tier)
        }
    }

    private fun updateDyeStats(tier : Int) {
        if (tier !in 1..5) return
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.SANGRIA]?.statistics ?: return

        DyeAddons.debug("Tracked Tier $tier vampire boss kill")
        stats.incrementInt("T$tier Riftstalker Bloodfiend Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..5) return
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.SANGRIA) ?: 1

        val baseOdds = when (tier) {
            1 -> 100_000
            2 -> 80_000
            3 -> 60_000
            4 -> 40_000
            5 -> 10_000
            else -> 0
        }
        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["vampire"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.VAMPIRE.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.SANGRIA]?.progress += (1.0 / baseOdds) * meterMultiplier * multiplier
    }

}