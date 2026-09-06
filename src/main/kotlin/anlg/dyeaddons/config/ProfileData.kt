package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.RngMeter

enum class DyeMultiplier {
    MAGIC_FIND,
    LOOTING,
    MAGIC_FIND_T1,
    LOOTING_T1,
    MAGIC_FIND_T2,
    LOOTING_T2,
    MAGIC_FIND_HORSEMAN,
    LOOTING_HORSEMAN,
    OVERBLOOM,
    METER,
    MIRACLE_CHANCE,
    BUCKET_OF_DYE,
    VINCENT,
}

class ProfileData {
    val dyeData : MutableMap<Dye, DyeData> = Dye.entries.associateWith { DyeData() }.toMutableMap()
    val dyeModifiers : MutableMap<String, Int> = mutableMapOf()
    val rngMeters : MutableMap<String, MeterData> = mutableMapOf()
    var visitorData : List<VisitorData> = listOf()
    val uniqueDyes : Int get() = dyeData.count { it.value.dropped >= 1 }

    /**
     * Gets the multiplier of the dye.
     * @param dye The dye for the multiplier
     * @param multipliers the multipliers to include
     */
    fun getDyeMultiplier(dye: Dye, vararg multipliers: DyeMultiplier): Float {
        val stats = dyeData[dye]?.statistics ?: return 1.0f

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0
        val magicFindT1 = stats["Magic Find on Common-Epic"]?.asFloat() ?: magicFind
        val lootingT1 = stats["Looting on Common-Epic"]?.asInt() ?: looting
        val magicFindT2 = stats["Magic Find on Legendary-Mythic"]?.asFloat() ?: magicFind
        val lootingT2 = stats["Looting on Legendary-Mythic"]?.asInt() ?: looting
        val magicFindHorseman = stats["Magic Find on Horseman"]?.asFloat() ?: magicFind
        val lootingHorseman = stats["Looting on Horseman"]?.asInt() ?: looting
        val overbloom = stats["Overbloom"]?.asFloat() ?: 0f
        val meterMultiplier = RngMeter.getDyeMultiplier(dye)?.toFloat() ?: 1.0f
        val miracleChance = dyeModifiers["Miracle Chance"] ?: 0
        val bucketOfDye = dyeModifiers["Bucket Of Dye"] ?: 0
        val rotationMultiplier = ConfigManager.data.config.currentDyeRotation?.getMultiplier(dye) ?: 1

        var multiplier = 1.0f

        if (DyeMultiplier.MAGIC_FIND in multipliers)
            multiplier *= 1.0f + magicFind / 100.0f

        if (DyeMultiplier.LOOTING in multipliers)
            multiplier *= 1.0f + looting * 0.15f

        if (DyeMultiplier.MAGIC_FIND_T1 in multipliers)
            multiplier *= 1.0f + magicFindT1 / 100.0f

        if (DyeMultiplier.LOOTING_T1 in multipliers)
            multiplier *= 1.0f + lootingT1 * 0.15f

        if (DyeMultiplier.MAGIC_FIND_T2 in multipliers)
            multiplier *= 1.0f + magicFindT2 / 100.0f

        if (DyeMultiplier.LOOTING_T2 in multipliers)
            multiplier *= 1.0f + lootingT2 * 0.15f

        if (DyeMultiplier.MAGIC_FIND_HORSEMAN in multipliers)
            multiplier *= 1.0f + magicFindHorseman / 100.0f

        if (DyeMultiplier.LOOTING_HORSEMAN in multipliers)
            multiplier *= 1.0f + lootingHorseman * 0.15f

        if (DyeMultiplier.OVERBLOOM in multipliers)
            multiplier *= 1.0f + overbloom / 100.0f

        if (DyeMultiplier.METER in multipliers)
            multiplier *= meterMultiplier

        if (DyeMultiplier.MIRACLE_CHANCE in multipliers)
            multiplier *= 1.0f + miracleChance / 1000.0f

        if (DyeMultiplier.BUCKET_OF_DYE in multipliers)
            multiplier *= 1.0f + bucketOfDye / 100.0f

        if (DyeMultiplier.VINCENT in multipliers)
            multiplier *= rotationMultiplier

        return multiplier
    }

    /**
     * Gets the magic find/overbloom of a dye and tier. if no magic find/overbloom exists for the dye then it returns 0.
     * @param dye The dye for the magic find.
     * @param tier Which tier of magic find/ if overbloom.
     */
    fun getMagicFind(dye: Dye, tier: DyeMultiplier): Float {
        val stats = dyeData[dye]?.statistics ?: return 0f

        val defaultMagicFind = stats["Magic Find"]?.asFloat() ?: 0f

        return when (tier) {
            DyeMultiplier.MAGIC_FIND -> stats["Magic Find"]?.asFloat() ?: 0f
            DyeMultiplier.MAGIC_FIND_T1 -> stats["Magic Find on Common-Epic"]?.asFloat() ?: defaultMagicFind
            DyeMultiplier.MAGIC_FIND_T2 -> stats["Magic Find on Legendary-Mythic"]?.asFloat() ?: defaultMagicFind
            DyeMultiplier.MAGIC_FIND_HORSEMAN -> stats["Magic Find on Horseman"]?.asFloat() ?: defaultMagicFind
            DyeMultiplier.OVERBLOOM -> stats["Overbloom"]?.asFloat() ?: 0f
            else -> 0f
        }
    }
}