package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.SkyblockTime

data class DyeRotation(
    val multipliers : Map<Dye, Int> = mapOf(),
    val year : Int = 0
) {
    fun getMultiplier(dye : Dye) : Int {
        return if (SkyblockTime.now().year == year) multipliers[dye] ?: 1 else 1
    }
}