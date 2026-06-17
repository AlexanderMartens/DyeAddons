package anlg.dyeaddons.utils.calc

enum class TrapperAnimal (val baseChance : Float){
    TRACKABLE(250_000f),
    UNTRACKABLE(200_000f),
    UNDETECTED(150_000f),
    ENDANGERED(100_000f),
    ELUSIVE(10_000f)
}

class TrapperTable(tracking : Float, crest : Boolean) {

    private val table = mutableMapOf(
        TrapperAnimal.TRACKABLE to 240f,
        TrapperAnimal.UNTRACKABLE to 180f,
        TrapperAnimal.UNDETECTED to 120f,
        TrapperAnimal.ENDANGERED to 60f,
        TrapperAnimal.ELUSIVE to 6f)

    init {
        table.replaceAll { key, value ->
            value * (if (key >= TrapperAnimal.ENDANGERED && crest) 1.25f else 1.0f) *
                    (if (key >= TrapperAnimal.ELUSIVE) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalWeight() : Float {
        return table.values.sumOf { it.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.entries.sumOf {(animal, weight) ->
            (weight / animal.baseChance).toDouble()
        }.toFloat()
    }

    fun getExpectedDistribution(mobs : Int) : Map<TrapperAnimal, Int> {
        return table.mapValues { (it.value / totalWeight() * mobs.toFloat()).toInt() }
    }
}