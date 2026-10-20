package anlg.dyeaddons.config

import anlg.dyeaddons.data.CalcValue

data class DyeData(
    var dropped : Int = 0,
    var progress : Double = 0.0,
    val statistics : MutableMap<String, CalcValue> = mutableMapOf(),
    val dyesDropped : MutableList<DyeDropped> = mutableListOf(),
) {
    /**
     * Adds a (Legacy) tag to the end of all statistics in the list.
     * @param stats List of statistics to migrate
     */
    fun migrateStatsToLegacy(stats: List<String>) {
        statistics.filter { (it.key.replace(Regex(""" \(\dx\)"""), "")) in stats }.keys.forEach { stat ->
            statistics.remove(stat)?.let { value ->
                statistics["$stat (Legacy)"] = value
            }
        }
    }
}

data class DyeDropped(
    val timestamp: Long,
    val progress: Double,
)