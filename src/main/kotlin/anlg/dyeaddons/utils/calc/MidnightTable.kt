package anlg.dyeaddons.utils.calc

enum class SpookySeaCreature (val baseChance : Float){
    COMMON(1_000_000f),
    UNCOMMON(1_000_000f),
    RARE(1_000_000f),
    EPIC(1_000_000f),
    LEGENDARY(50_000f),
    MYTHIC(50_000f)
}

data class SpookySeaCreatureItem (
    val name : String,
    val rarity : SpookySeaCreature,
    var weight : Float
)

class MidnightTable(whaleBait : Boolean, tracking : Float) {

    private val table = listOf(
        SpookySeaCreatureItem("Jumpin' Jack", SpookySeaCreature.COMMON, 5_000f),
        SpookySeaCreatureItem("Scarecrow", SpookySeaCreature.UNCOMMON, 2_875f),
        SpookySeaCreatureItem("Nightmare", SpookySeaCreature.RARE, 1_500f),
        SpookySeaCreatureItem("Werewolf", SpookySeaCreature.EPIC, 500f),
        SpookySeaCreatureItem("Phantom Fisher", SpookySeaCreature.LEGENDARY, 100f),
        SpookySeaCreatureItem("Grim Reaper", SpookySeaCreature.MYTHIC, 25f)
    )

    init {
        table.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= SpookySeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { creature ->
            (creature.weight / creature.rarity.baseChance).toDouble()
        }.toFloat()
    }
}