package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.RngMeter
import anlg.dyeaddons.utils.extensions.incrementInt

enum class DyeMultiplier {
    MAGIC_FIND,
    OVERBLOOM,
    METER,
    MIRACLE_CHANCE,
    BUCKET_OF_DYE,
    PAINT_PALETTE,
    FOUR_LEAF_CLOVER,
    VINCENT,
}

class ProfileData {
    val dyeData : MutableMap<Dye, DyeData> = Dye.entries.associateWith { DyeData() }.toMutableMap()
    val dyeModifiers : MutableMap<String, Int> = mutableMapOf()
    val rngMeters : MutableMap<String, MeterData> = mutableMapOf()
    var visitorData : List<VisitorData> = listOf()
    val uniqueDyes : Int get() = dyeData.count { it.value.dropped >= 1 }
    val rotationData : MutableMap<Int, RotationData> = mutableMapOf()

    /**
     * Gets the multiplier of the dye.
     * @param dye The dye for the multiplier
     * @param multipliers the multipliers to include
     */
    fun getDyeMultiplier(dye: Dye, vararg multipliers: DyeMultiplier): Float {
        val stats = dyeData[dye]?.statistics ?: return 1.0f

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val overbloom = stats["Overbloom"]?.asFloat() ?: 0f
        val meterMultiplier = RngMeter.getDyeMultiplier(dye)?.toFloat() ?: 1.0f
        val miracleChance = dyeModifiers["Miracle Chance"] ?: 0
        val bucketOfDye = dyeModifiers["Bucket Of Dye"] ?: 0
        val paintPalette = dyeModifiers["Paint Palette"] ?: 0
        val fourLeafClover = dyeModifiers["Four-leaf Clover"] ?: 0
        val rotationMultiplier = ConfigManager.data.config.currentDyeRotation?.getMultiplier(dye) ?: 1

        var multiplier = 1.0f

        if (DyeMultiplier.MAGIC_FIND in multipliers)
            multiplier *= 1.0f + magicFind / 100.0f

        if (DyeMultiplier.OVERBLOOM in multipliers)
            multiplier *= 1.0f + overbloom / 100.0f

        if (DyeMultiplier.METER in multipliers)
            multiplier *= meterMultiplier

        if (DyeMultiplier.MIRACLE_CHANCE in multipliers)
            multiplier *= 1.0f + miracleChance / 1000.0f

        if (DyeMultiplier.BUCKET_OF_DYE in multipliers)
            multiplier *= 1.0f + bucketOfDye / 100.0f

        if (DyeMultiplier.PAINT_PALETTE in multipliers)
            multiplier *= 1.0f + paintPalette / 100.0f

        if (DyeMultiplier.FOUR_LEAF_CLOVER in multipliers)
            multiplier *= 1.0f + fourLeafClover / 25.0f

        if (DyeMultiplier.VINCENT in multipliers)
            multiplier *= rotationMultiplier

        return multiplier
    }

    /**
     * Gets the multiplier of the dye. Assumes all possible modifiers affect it (including vincent).
     * @param dye The dye for the multiplier
     */
    fun getDyeMultiplier(dye: Dye): Float {
        val stats = dyeData[dye]?.statistics ?: return 1.0f

        val multipliers = mutableListOf<DyeMultiplier>()

        if (dye in listOf(Dye.CHOCOLATE, Dye.BINGO_BLUE, Dye.PURE_BLUE, Dye.PURE_WHITE, Dye.PURE_BLACK, Dye.PURE_YELLOW))
            return 1.0f

        multipliers.add(DyeMultiplier.VINCENT)

        if (dye != Dye.DARK_PURPLE)
            multipliers.add(DyeMultiplier.PAINT_PALETTE)

        if (stats.keys.contains("Magic Find"))
            multipliers.add(DyeMultiplier.MAGIC_FIND)

        if (stats.keys.contains("Overbloom"))
            multipliers.add(DyeMultiplier.OVERBLOOM)

        if (RngMeter.entries.any { it.dye == dye })
            multipliers.add(DyeMultiplier.METER)

        if (dye !in listOf(Dye.SANGRIA, Dye.CELADON, Dye.DARK_PURPLE)) { // Rift dyes and dark auction
            multipliers.add(DyeMultiplier.MIRACLE_CHANCE)
            multipliers.add(DyeMultiplier.BUCKET_OF_DYE)
            multipliers.add(DyeMultiplier.FOUR_LEAF_CLOVER)
        }

        return getDyeMultiplier(dye, *multipliers.toTypedArray())
    }

    /**
     * Gets the magic find/overbloom of a dye and tier. if no magic find/overbloom exists for the dye then it returns 0.
     * @param dye The dye for the magic find.
     * @param type Which type of magic find/overbloom.
     */
    fun getMagicFind(dye: Dye, type: DyeMultiplier): Float {
        val stats = dyeData[dye]?.statistics ?: return 0f

        return when (type) {
            DyeMultiplier.MAGIC_FIND -> stats["Magic Find"]?.asFloat() ?: 0f
            DyeMultiplier.OVERBLOOM -> stats["Overbloom"]?.asFloat() ?: 0f
            else -> 0f
        }
    }

    /**
     * Increments a stat in a dye. If the dye is buffed by vincent, then it also increments the multiplied stat.
     */
    fun incrementStat(dye: Dye, stat: String) {
        val rotationMultiplier = ConfigManager.data.config.currentDyeRotation?.getMultiplier(dye)
        val stats = dyeData[dye]?.statistics ?: return

        stats.incrementInt(stat)
        when (rotationMultiplier) {
            2 -> stats.incrementInt("$stat (2x)")
            3 -> stats.incrementInt("$stat (3x)")
        }
    }
}