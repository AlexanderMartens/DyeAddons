package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye

// Second dye is 3x, the others are 2x
data class DyeRotation(
    val dyes : List<DyeInRotation>,
    val year : Int
)

data class DyeInRotation(
    val dye : Dye,
    val multiplier : Int,
)