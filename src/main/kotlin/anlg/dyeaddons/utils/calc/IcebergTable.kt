package anlg.dyeaddons.utils.calc

enum class WinterSeaCreature (val baseChance : Float){
    COMMON(2_500_000f),
    UNCOMMON(2_500_000f),
    RARE(500_000f),
    EPIC(500_000f),
    LEGENDARY(50_000f),
    MYTHIC(0f)
}

data class WinterSeaCreatureItem (
    val name : String,
    val rarity : WinterSeaCreature,
    var weight : Float
)

class IcebergTable(whaleBait : Boolean, tracking : Float, drakePiper : Boolean = false) {

    private val table = listOf(
        WinterSeaCreatureItem("Frozen Steve", WinterSeaCreature.COMMON, 5_000f),
        WinterSeaCreatureItem("Frosty", WinterSeaCreature.UNCOMMON, 2_875f),
        WinterSeaCreatureItem("Grinch", WinterSeaCreature.RARE, 1_500f),
        WinterSeaCreatureItem("Nutcracker", WinterSeaCreature.EPIC, 500f),
        WinterSeaCreatureItem("Yeti", WinterSeaCreature.LEGENDARY, 100f),
        WinterSeaCreatureItem("Reindrake", WinterSeaCreature.MYTHIC, 25f * if (drakePiper) 1.1f else 1f)
    )

    init {
        table.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= WinterSeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { creature ->
            if (creature.rarity.baseChance == 0f) return@sumOf 0.0
            (creature.weight / creature.rarity.baseChance).toDouble()
        }.toFloat()
    }
}