package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

object JadeTracker {

    private val NUCLEUS_PATTERN = Regex("""CRYSTAL NUCLEUS LOOT BUNDLE""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crystal Hollows") return

        if (NUCLEUS_PATTERN.matches(event.unformattedText.trim())) {
            val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["nucleus"]
            val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
            val addedMeter = (1f + pityShard / 100f) * 1_000f
            meter?.progress += addedMeter.toInt()

            updateDyeStats()
            updateDyeProgress()
        }
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.statistics ?: return

        stats.incrementInt("Nucleus Runs Completed")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.JADE) ?: 1

        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.statistics

        val molePet = stats?.get("Mole Pet Level")?.asInt() ?: 0
        val highRoller = stats?.get("High Roller Perk")?.asBool() ?: false
        val biggerBox = stats?.get("Bigger Box Level")?.asInt() ?: 0
        val echoBox = stats?.get("Echo of Box Level")?.asInt() ?: 0
        val echoEcho = stats?.get("Echo of Echo Level")?.asInt() ?: 0

        var extraItems = 0.0
        extraItems += molePet / 100.0
        if (highRoller) extraItems += 1.0
        extraItems += (biggerBox / 20.0) * (1.0 + (echoBox / 50.0) * (1.0 + (echoEcho / 20.0)))

        val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["nucleus"]
        val meterSelected = meter?.selected ?: false
        val meterProgress = meter?.progress ?: 0
        val meterMultiplier = if (meterSelected) RngMeter.NUCLEUS.getDyeMultiplier(meterProgress) else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.progress += (1.0 / 500_000.0) * (17.0 + extraItems) * meterMultiplier * multiplier
    }

}