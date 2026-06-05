package anlg.dyeaddons.utils.calc

enum class Treasure (val baseChance : Float){
    GOOD(1_000_000f),
    GREAT(100_000f),
    OUTSTANDING(10_000f)
}

// I'm assuming treasures use the weight system, but it could also be outstanding rolls first, then great, then it's good
data class TreasureItem (
    val rarity : Treasure,
    var weight : Float
)

class TreasureTable(hermitCrab : Float, blessing : Int, blessedBait : Boolean) {

    private val table = listOf(
        TreasureItem(Treasure.GOOD, 89f),
        TreasureItem(Treasure.GREAT, 10f),
        TreasureItem(Treasure.OUTSTANDING, 1f)
    )

    init {
        table.forEach { treasure ->
            treasure.weight *= (if (treasure.rarity >= Treasure.GREAT) (1f + hermitCrab/100f) * (1f + blessing * 0.02f) *
                    (if (blessedBait) 1.5f else 1f) else 1f)

        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { treasure ->
            (treasure.weight / treasure.rarity.baseChance).toDouble()
        }.toFloat()
    }
}