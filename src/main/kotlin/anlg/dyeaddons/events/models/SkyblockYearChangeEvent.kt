package anlg.dyeaddons.events.models

/**
 * Called when the skyblock year changes.
 */
data class SkyblockYearChangeEvent (
    val oldYear: Int,
    val newYear: Int,
)