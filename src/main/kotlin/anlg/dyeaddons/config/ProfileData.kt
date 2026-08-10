package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.calc.RngMeter

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
        val meterMultiplier = when (dye) {
            Dye.MATCHA -> if (rngMeters["zombie"]?.selected == true) {
                RngMeter.ZOMBIE.getDyeMultiplier(rngMeters["zombie"]?.progress ?: 0)
            } else 1.0f
            Dye.BRICK_RED -> if (rngMeters["spider"]?.selected == true) {
                RngMeter.SPIDER.getDyeMultiplier(rngMeters["spider"]?.progress ?: 0)
            } else 1.0f
            Dye.CELESTE -> if (rngMeters["wolf"]?.selected == true) {
                RngMeter.WOLF.getDyeMultiplier(rngMeters["wolf"]?.progress ?: 0)
            } else 1.0f
            Dye.BYZANTIUM -> if (rngMeters["enderman"]?.selected == true) {
                RngMeter.ENDERMAN.getDyeMultiplier(rngMeters["enderman"]?.progress ?: 0)
            } else 1.0f
            Dye.FLAME -> if (rngMeters["blaze"]?.selected == true) {
                RngMeter.BLAZE.getDyeMultiplier(rngMeters["blaze"]?.progress ?: 0)
            } else 1.0f
            Dye.SANGRIA -> if (rngMeters["vampire"]?.selected == true) {
                RngMeter.VAMPIRE.getDyeMultiplier(rngMeters["vampire"]?.progress ?: 0)
            } else 1.0f
            Dye.LIVID -> if (rngMeters["m5"]?.selected == true) {
                RngMeter.M5.getDyeMultiplier(rngMeters["m5"]?.progress ?: 0)
            } else 1.0f
            Dye.NECRON -> if (rngMeters["m7"]?.selected == true) {
                RngMeter.M7.getDyeMultiplier(rngMeters["m7"]?.progress ?: 0)
            } else 1.0f
            Dye.JADE -> if (rngMeters["nucleus"]?.selected == true) {
                RngMeter.NUCLEUS.getDyeMultiplier(rngMeters["nucleus"]?.progress ?: 0)
            } else 1.0f
            Dye.FROSTBITTEN -> if (rngMeters["frozenCorpse"]?.selected == true) {
                RngMeter.FROZEN_CORPSE.getDyeMultiplier(rngMeters["frozenCorpse"]?.progress ?: 0)
            } else 1.0f
            Dye.NADESHIKO -> if (rngMeters["experimentation"]?.selected == true) {
                RngMeter.EXPERIMENTATION.getDyeMultiplier(rngMeters["experimentation"]?.progress ?: 0)
            } else 1.0f
            else -> 1.0f
        }.toFloat()
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