package anlg.dyeaddons.utils.calc

enum class MythologicalCreature (val baseChance : Float){
    COMMON(1_000_000f),
    UNCOMMON(1_000_000f),
    RARE(500_000f),
    EPIC(250_000f),
    LEGENDARY(50_000f),
    MYTHIC(10_000f)
}

data class MythologicalCreatureItem (
    val name : String,
    val rarity : MythologicalCreature,
    var weight : Float
)

class MythologicalTable(tracking : Float, griffinRarity : MythologicalCreature) {

    private val table = listOf(
        MythologicalCreatureItem("Minos Hunter", MythologicalCreature.COMMON, 1_000f),
        MythologicalCreatureItem("Siamese Lynxes", MythologicalCreature.COMMON, 1_000f),
        MythologicalCreatureItem("Stranded Nymph", MythologicalCreature.UNCOMMON, 800f),
        MythologicalCreatureItem("Cretan Bull", MythologicalCreature.UNCOMMON, 800f),
        MythologicalCreatureItem("Harpy", MythologicalCreature.RARE, 600f),
        MythologicalCreatureItem("Gaia Construct", MythologicalCreature.RARE, 600f),
        MythologicalCreatureItem("Minotaur", MythologicalCreature.EPIC, 400f),
        MythologicalCreatureItem("Minos Champion", MythologicalCreature.EPIC, 400f),
        MythologicalCreatureItem("Sphinx", MythologicalCreature.LEGENDARY, 75f),
        MythologicalCreatureItem("Minos Inquisitor", MythologicalCreature.LEGENDARY, 75f),
        MythologicalCreatureItem("Manticore", MythologicalCreature.MYTHIC, 15f),
        MythologicalCreatureItem("King Minos", MythologicalCreature.MYTHIC, 15f)
    )

    init {
        table.forEach { creature ->
            creature.weight *= (if (creature.rarity >= MythologicalCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f) *
                    if (creature.rarity > griffinRarity) 0.0f else 1.0f
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