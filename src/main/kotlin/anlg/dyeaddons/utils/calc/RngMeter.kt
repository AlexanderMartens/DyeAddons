package anlg.dyeaddons.utils.calc

import kotlin.math.min

enum class RngMeter(val maxMeter: Int) {
    ZOMBIE(75_000_000),
    SPIDER(75_000_000),
    WOLF(75_000_000),
    ENDERMAN(75_000_000),
    BLAZE(75_000_000),
    VAMPIRE(750_000),
    EXPERIMENTATION(2_500_000),
    NUCLEUS(5_000_000),
    FROZEN_CORPSE(5_000_000),
    M5(1_000_000),
    M7(1_000_000);

    fun getDyeMultiplier(progress: Int): Double {
        return 1 + min(2 * progress.toDouble() / maxMeter, 2.0)
    }
}