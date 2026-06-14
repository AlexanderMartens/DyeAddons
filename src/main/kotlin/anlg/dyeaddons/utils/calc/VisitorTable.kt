package anlg.dyeaddons.utils.calc

enum class Visitor (val baseChance : Float){
    UNCOMMON(100_000f),
    RARE(50_000f),
    LEGENDARY(25_000f),
    MYTHIC(5_000f),
    SPECIAL(500f)
}

data class VisitorItem (
    val rarity : Visitor,
    var weight : Float
)

class VisitorTable(bloomingBusiness: Boolean, fancyVisit : Int = 0, copperTalisman : Int = 0) {

    private val rareMultiplier = 1f * (1f + fancyVisit / 100f) * (1f + copperTalisman / 25f) * if (bloomingBusiness) 1.2f else 1f
    private val table = listOf(
        VisitorItem(Visitor.UNCOMMON, 50f),
        VisitorItem(Visitor.RARE, 15f),
        VisitorItem(Visitor.LEGENDARY, 3f),
        VisitorItem(Visitor.MYTHIC, 0.3f),
        VisitorItem(Visitor.SPECIAL, 0.03f)
    )

    init {
        table.forEach { visitor ->
            visitor.weight *= (if (visitor.rarity >= Visitor.RARE) rareMultiplier else 1f)
        }
    }

    private fun totalWeight() : Float {
        return table.sumOf { it.weight.toDouble() }.toFloat()
    }

    fun getAverageDropChance() : Float {
        return totalWeight() / table.sumOf { visitor ->
            (visitor.weight / visitor.rarity.baseChance).toDouble()
        }.toFloat()
    }
}