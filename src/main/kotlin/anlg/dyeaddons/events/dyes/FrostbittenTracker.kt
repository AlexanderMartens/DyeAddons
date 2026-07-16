package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

enum class FrozenCorpse {
    LAPIS,
    UMBER,
    TUNGSTEN,
    VANGUARD,
}

object FrostbittenTracker {

    private val LAPIS_CORPSE_PATTERN = Regex("""LAPIS CORPSE LOOT!""")

    private val UMBER_CORPSE_PATTERN = Regex("""UMBER CORPSE LOOT!""")

    private val TUNGSTEN_CORPSE_PATTERN = Regex("""TUNGSTEN CORPSE LOOT!""")

    private val VANGUARD_CORPSE_PATTERN = Regex("""VANGUARD CORPSE LOOT!""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Mineshaft") return

        var baseAddedMeter : Int? = null
        if (LAPIS_CORPSE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(FrozenCorpse.LAPIS)
            updateDyeProgress(FrozenCorpse.LAPIS)
            baseAddedMeter = 500
        }
        if (UMBER_CORPSE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(FrozenCorpse.UMBER)
            updateDyeProgress(FrozenCorpse.UMBER)
            baseAddedMeter = 2_500
        }
        if (TUNGSTEN_CORPSE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(FrozenCorpse.TUNGSTEN)
            updateDyeProgress(FrozenCorpse.TUNGSTEN)
            baseAddedMeter = 2_500
        }
        if (VANGUARD_CORPSE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(FrozenCorpse.VANGUARD)
            updateDyeProgress(FrozenCorpse.VANGUARD)
            baseAddedMeter = 25_000
        }
        if (baseAddedMeter == null) return
        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["nucleus"]
        val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
        val addedMeter = (1f + pityShard / 100f) * baseAddedMeter
        meter?.progress += addedMeter.toInt()

    }

    private fun updateDyeStats(corpse: FrozenCorpse) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FROSTBITTEN]?.statistics ?: return

        DyeAddons.debug("Tracked $corpse corpse")
        when (corpse) {
            FrozenCorpse.LAPIS -> stats.incrementInt("Lapis Corpses Looted")
            FrozenCorpse.UMBER -> stats.incrementInt("Umber/Tungsten Corpses Looted")
            FrozenCorpse.TUNGSTEN -> stats.incrementInt("Umber/Tungsten Corpses Looted")
            FrozenCorpse.VANGUARD -> stats.incrementInt("Vanguard Corpses Looted")
        }
    }

    private fun updateDyeProgress(corpse: FrozenCorpse) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.FROSTBITTEN) ?: 1

        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FROSTBITTEN]?.statistics
        val hotmPerk = stats?.get("Gifts from the Departed Perk")?.asInt() ?: 0
        val milestone = stats?.get("Frozen Corpse Milestone")?.asInt() ?: 0

        var extraItems = 0.0
        extraItems += hotmPerk / 500.0
        if (milestone >= 3) extraItems += 0.1
        if (milestone >= 6) extraItems += 0.1

        val (dropChance, rolls) = when (corpse) {
            FrozenCorpse.LAPIS -> 250_000.0 to 4.5
            FrozenCorpse.UMBER -> 100_000.0 to 5.5
            FrozenCorpse.TUNGSTEN -> 100_000.0 to 5.5
            FrozenCorpse.VANGUARD -> 10_000.0 to 6.5
        }

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.FROSTBITTEN]?.progress += (1.0 / dropChance) * (rolls + extraItems) * multiplier
    }

}
