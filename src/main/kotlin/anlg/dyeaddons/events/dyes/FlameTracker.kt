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

object FlameTracker {

    private val SLAYER_BOSS_COMPLETE_PATTERN = Regex("""SLAYER QUEST COMPLETE!""")


    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        if (SLAYER_BOSS_COMPLETE_PATTERN.matches(event.unformattedText.trim())) {
            val tier = when(ScoreboardUtils.getLineAfter("Inferno Demonlord")) {
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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FLAME]?.statistics ?: return

        DyeAddons.debug("Tracked Tier $tier blaze boss kill")
        stats.incrementInt("T$tier Inferno Demonlord Kills")
    }

    private fun updateDyeProgress(tier : Int) {
        if (tier !in 1..5) return
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.FLAME) ?: 1

        val baseOdds = when (tier) {
            1 -> 10_000_000
            2 -> 2_500_000
            3 -> 1_000_000
            4 -> 500_000
            5 -> 250_000
            else -> 0
        }
        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["blaze"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.BLAZE.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FLAME]?.progress += (1.0 / baseOdds) * meterMultiplier * multiplier
    }

}