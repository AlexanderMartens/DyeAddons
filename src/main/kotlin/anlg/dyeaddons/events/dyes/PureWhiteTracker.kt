package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils

object PureWhiteTracker {

    private const val TICKS_PER_UPDATE = 20
    private var tickCounter = 0

    fun init() {
        EventBus.subscribe(ClientTickEvent::class, ::onClientTick)
    }


    private fun onClientTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        tickCounter++
        if (tickCounter < TICKS_PER_UPDATE) return
        tickCounter = 0

        if (SkyblockUtils.hypixelMain && SkyblockUtils.isInSkyblock()) {
            updateDyeStats()
            updateDyeProgress()
        }
    }

    private fun updateDyeStats() {
        val bits = getBits() ?: return

        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PURE_WHITE]?.statistics

        stats?.set("Bits", CalcValue.IntVal(bits))
    }

    private fun updateDyeProgress() {
        val bits = getBits() ?: return

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PURE_WHITE]?.progress = bits / 250_000.0
    }

    private fun getBits() : Int? {
        return ScoreboardUtils.getLineAfter("Bits:").replace(",","").toIntOrNull()
    }

}