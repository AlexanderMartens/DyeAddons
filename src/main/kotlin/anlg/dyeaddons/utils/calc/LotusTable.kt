package anlg.dyeaddons.utils.calc

// Could be generalized to most sea creature tables
class LotusTable(whaleBait : Boolean, tracking : Float, puddleJumperHook : Boolean = false) {

    private val table = listOf(
        WaterSeaCreatureItem("Atoll Croaker", WaterSeaCreature.COMMON, 5_000f),
        WaterSeaCreatureItem("Lotus Guardian", WaterSeaCreature.UNCOMMON, 2_875f),
        WaterSeaCreatureItem("gorF", WaterSeaCreature.RARE, 1_500f),
        WaterSeaCreatureItem("Drowned Captain", WaterSeaCreature.EPIC, 500f),
        WaterSeaCreatureItem("Puddle Jumper", WaterSeaCreature.LEGENDARY, 100f),
        WaterSeaCreatureItem("Frog Prince", WaterSeaCreature.MYTHIC, 25f)
    )

    init {
        table.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= WaterSeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f) *
                    (if (creature.name == "Puddle Jumper" && puddleJumperHook) 5.0f else 1.0f)
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