package anlg.dyeaddons.utils.calc

enum class TrapperAnimal (val baseChance : Float){
    TRACKABLE(250_000f),
    UNTRACKABLE(200_000f),
    UNDETECTED(150_000f),
    ENDANGERED(100_000f),
    ELUSIVE(10_000f)
}

data class TrapperAnimalItem (
    val rarity : TrapperAnimal,
    var weight : Float
)

class TrapperTable(tracking : Float, crest : Boolean) {

    private val table = listOf(
        TrapperAnimalItem(TrapperAnimal.TRACKABLE, 240f),
        TrapperAnimalItem(TrapperAnimal.UNTRACKABLE, 180f),
        TrapperAnimalItem(TrapperAnimal.UNDETECTED, 120f),
        TrapperAnimalItem(TrapperAnimal.ENDANGERED, 60f),
        TrapperAnimalItem(TrapperAnimal.ELUSIVE, 6f))

    init {
        table.forEach { animal ->
            animal.weight *= (if (animal.rarity >= TrapperAnimal.ENDANGERED && crest) 1.25f else 1.0f) *
                    (if (animal.rarity >= TrapperAnimal.ELUSIVE) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { animal ->
            (animal.weight / animal.rarity.baseChance).toDouble()
        }.toFloat()
    }
}