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
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object TentacleTracker {

    private val KUUDRA_DOWN_PATTERN = Regex("""KUUDRA DOWN!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(KismetUsedEvent::class, ::onKismetUsed)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (KUUDRA_DOWN_PATTERN.matches(event.unformattedText.trim())) {
            val tier = ScoreboardUtils.getLineAfter("Kuudra's Hollow")
            updateDyeStats(tier)
            updateDyeProgress(tier)
            DyeAddons.debug("Tracked $tier tier kuudra completed")
        }
    }

    private fun onKismetUsed(event: KismetUsedEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        if (event.chestType != ChestType.PAID) return

        val tier = when (event.instanceType) {
            InstanceType.KUUDRA_BASIC -> "(T1)"
            InstanceType.KUUDRA_HOT -> "(T2)"
            InstanceType.KUUDRA_BURNING -> "(T3)"
            InstanceType.KUUDRA_FIERY -> "(T4)"
            InstanceType.KUUDRA_INFERNAL -> "(T5)"
            else -> return
        }
        updateDyeStats(tier)
        updateDyeProgress(tier)
    }

    private fun updateDyeStats(tier: String) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.TENTACLE]?.statistics ?: return

        when (tier) {
            "(T1)" -> stats.incrementInt("Basic Kuudra Completions + Kismets Used")
            "(T2)" -> stats.incrementInt("Hot Kuudra Completions + Kismets Used")
            "(T3)" -> stats.incrementInt("Burning Kuudra Completions + Kismets Used")
            "(T4)" -> stats.incrementInt("Fiery Kuudra Completions + Kismets Used")
            "(T5)" -> stats.incrementInt("Infernal Kuudra Completions + Kismets Used")
        }
    }

    private fun updateDyeProgress(tier: String) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.TENTACLE) ?: 1

        val baseChance = when (tier) {
            "(T1)" -> 100_000
            "(T2)" -> 80_000
            "(T3)" -> 60_000
            "(T4)" -> 40_000
            "(T5)" -> 20_000
            else -> { DyeAddons.debug("Could not determine kuudra tier"); return }
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.TENTACLE]?.progress += (1.0 / baseChance) * multiplier
    }

}