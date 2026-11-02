package anlg.dyeaddons.utils.calc

import anlg.dyeaddons.data.Dye

enum class Treasure (val baseChance : Double){
    GOOD(Dye.TREASURE.getDropRate("Good")),
    GREAT(Dye.TREASURE.getDropRate("Great")),
    OUTSTANDING(Dye.TREASURE.getDropRate("Outstanding")),
}

// I'm assuming treasures use the weight system, but it could also be outstanding rolls first, then great, then it's good
data class TreasureItem (
    val rarity : Treasure,
    var weight : Float
)

class TreasureTable(greatBuff: Float, outstandingBuff: Float) {

    private val table = listOf(
        TreasureItem(Treasure.GOOD, 89f),
        TreasureItem(Treasure.GREAT, 10f),
        TreasureItem(Treasure.OUTSTANDING, 1f)
    )

    init {
        table.forEach { treasure ->
            when (treasure.rarity) {
                Treasure.GOOD -> {}
                Treasure.GREAT -> treasure.weight *= greatBuff
                Treasure.OUTSTANDING -> treasure.weight *= outstandingBuff
            }

        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { treasure ->
            (treasure.weight / treasure.rarity.baseChance)
        }.toFloat()
    }
}