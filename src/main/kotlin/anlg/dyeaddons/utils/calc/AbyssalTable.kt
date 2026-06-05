package anlg.dyeaddons.utils.calc

enum class WaterSeaCreature (val baseChance : Float){
    COMMON(5_000_000f),
    UNCOMMON(5_000_000f),
    RARE(2_500_000f),
    EPIC(2_500_000f),
    LEGENDARY(50_000f),
    MYTHIC(50_000f)
}

data class WaterSeaCreatureItem (
    val name : String,
    val rarity : WaterSeaCreature,
    var weight : Float
)

class AbyssalTable(whaleBait : Boolean, tracking : Float) {

    private val table = listOf(
        WaterSeaCreatureItem("Squid", WaterSeaCreature.COMMON, 1_200f),
        WaterSeaCreatureItem("Sea Walker", WaterSeaCreature.COMMON, 800f),
        WaterSeaCreatureItem("Sea Witch", WaterSeaCreature.UNCOMMON, 700f),
        WaterSeaCreatureItem("Sea Archer", WaterSeaCreature.UNCOMMON, 550f),
        WaterSeaCreatureItem("Rider of the Deep", WaterSeaCreature.UNCOMMON, 400f),
        WaterSeaCreatureItem("Catfish", WaterSeaCreature.RARE, 250f),
        WaterSeaCreatureItem("Sea Leech", WaterSeaCreature.RARE, 160f),
        WaterSeaCreatureItem("Guardian Defender", WaterSeaCreature.EPIC, 130f),
        WaterSeaCreatureItem("Deep Sea Protector", WaterSeaCreature.EPIC, 88f),
        WaterSeaCreatureItem("Water Hydra", WaterSeaCreature.LEGENDARY, 18f),
        WaterSeaCreatureItem("Abyssal Miner", WaterSeaCreature.LEGENDARY, 90f)
    )

    init {
        table.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= WaterSeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { creature ->
            (creature.weight / creature.rarity.baseChance *
                    if (creature.name == "Water Hydra") 2f else 1f).toDouble()
        }.toFloat()
    }
}