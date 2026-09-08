package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye

data class RotationData(
    val multipliers : Map<Dye, Int> = mapOf(),
    val startDyeData: Map<Dye, DyeData> = mapOf(),
    var endDyeData: Map<Dye, DyeData> = mapOf()
)
