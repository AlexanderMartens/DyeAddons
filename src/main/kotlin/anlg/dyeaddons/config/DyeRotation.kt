package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.SkyblockTime

// Second dye is 3x, the others are 2x
data class DyeRotation(
    val multipliers : Map<Dye, Int>,
    val year : Int
) {
    fun getMultiplier(dye : Dye) : Int {
        return if (SkyblockTime.now().year == year) multipliers[dye] ?: 1 else 1
    }
}