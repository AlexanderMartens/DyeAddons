package anlg.dyeaddons.data

/**
 * The player's collection of dyes
 */
data class DyeData(
    val dyes: List<Dye> = Dye.entries.toList(),
)
