package anlg.dyeaddons.utils.calc

import anlg.dyeaddons.data.Dye

enum class Treasure (val baseChance : Float){
    GOOD(Dye.TREASURE.getDropRate("Good")),
    GREAT(Dye.TREASURE.getDropRate("Great")),
    OUTSTANDING(Dye.TREASURE.getDropRate("Outstanding")),
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