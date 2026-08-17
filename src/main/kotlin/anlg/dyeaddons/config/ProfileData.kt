package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.RngMeter

enum class DyeMultiplier {
    MAGIC_FIND,
    LOOTING,
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
    var visitorData :  List<VisitorData> = listOf()

    /**
     * Gets the multiplier of the dye.
     * @param dye The dye for the multiplier
     * @param multipliers the multipliers to include
     */
    fun getDyeMultiplier(dye: Dye, vararg multipliers: DyeMultiplier): Float {
        val stats = dyeData[dye]?.statistics ?: return 1.0f

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0
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
}