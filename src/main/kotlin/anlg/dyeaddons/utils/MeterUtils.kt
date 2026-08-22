package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.settings.categories.DebugCategories
import kotlin.math.min

enum class RngMeter(val maxMeter: Int, val meterName: String, val dye: Dye) {
    ZOMBIE(75_000_000, "zombie", Dye.MATCHA),
    SPIDER(75_000_000, "spider", Dye.BRICK_RED),
    WOLF(75_000_000, "wolf", Dye.CELESTE),
    ENDERMAN(75_000_000, "enderman", Dye.BYZANTIUM),
    BLAZE(75_000_000, "blaze", Dye.FLAME),
    VAMPIRE(750_000, "vampire", Dye.SANGRIA),
    EXPERIMENTATION(2_500_000, "experimentation", Dye.NADESHIKO),
    NUCLEUS(5_000_000, "nucleus", Dye.JADE),
    FROZEN_CORPSE(5_000_000, "frozenCorpse", Dye.FROSTBITTEN),
    M5(1_000_000, "m5", Dye.LIVID),
    M7(1_000_000, "m7", Dye.NECRON);

    fun getDyeMultiplier(progress: Int): Double {
        return 1 + min(2 * progress.toDouble() / maxMeter, 2.0)
    }

    companion object {
        fun getDyeMultiplier(dye: Dye): Double? {
            val meter = RngMeter.entries.firstOrNull { it.dye == dye } ?: return null

            val rngMeters = ProfileStorage.lastPlayedProfile()?.rngMeters ?: return null

            return if (rngMeters[meter.meterName]?.selected == true) {
                meter.getDyeMultiplier(rngMeters[meter.meterName]?.progress ?: 0)
            } else 1.0
        }

        fun getMeterProgress(dye: Dye): Double? {
            val meter = RngMeter.entries.firstOrNull { it.dye == dye } ?: return null

            val rngMeters = ProfileStorage.lastPlayedProfile()?.rngMeters ?: return null

            return rngMeters[meter.meterName]?.progress?.div(meter.maxMeter.toDouble())
        }

        /**
         * Call when you drop a dye. If the dye was selected and meter was at 100%, then add 100% to progress and
         * subtract maxMeter from meter.
         */
        fun guaranteedDye(dye: Dye) {
            val meterProgress = getMeterProgress(dye) ?: return
            val meter = RngMeter.entries.firstOrNull { it.dye == dye } ?: return
            val meterData = ProfileStorage.lastPlayedProfile()?.rngMeters[meter.meterName] ?: return

            if (meterProgress < 1.0 || !meterData.selected) return

            ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += 1.0
            meterData.progress -= meter.maxMeter
            DyeAddons.debug("Captured 100% metered dye for $dye", DebugCategories.DYE_EVENT)
        }
    }
}