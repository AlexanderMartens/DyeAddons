package anlg.dyeaddons.utils.calc

import anlg.dyeaddons.data.Dye

enum class Visitor (val baseChance : Float){
    UNCOMMON(Dye.COPPER.getDropRate("Uncommon")),
    RARE(Dye.COPPER.getDropRate("Rare")),
    LEGENDARY(Dye.COPPER.getDropRate("Legendary")),
    MYTHIC(Dye.COPPER.getDropRate("Mythic")),
    SPECIAL(Dye.COPPER.getDropRate("Special"))
}

data class VisitorItem (
    val rarity : Visitor,
    var weight : Float
)

class VisitorTable(bloomingBusiness: Boolean,
                   fancyVisit : Int = 0,
                   copperTalisman : Int = 0,
                   val charmedVisitors : Visitor? = null) {

    private val rareMultiplier = 1f * (1f + fancyVisit / 100f) * (1f + copperTalisman / 25f) * if (bloomingBusiness) 1.2f else 1f
    private val table = listOf(
        VisitorItem(Visitor.UNCOMMON, 50f),
        VisitorItem(Visitor.RARE, 15f),
        VisitorItem(Visitor.LEGENDARY, 3f),
        VisitorItem(Visitor.MYTHIC, 0.3f)
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
            (visitor.weight /
                    visitor.rarity.baseChance *
                    (if (charmedVisitors != null && visitor.rarity >= charmedVisitors) (1f + Dye.COPPER.getDropRate("Charm")) / 2f else 1f)
                    ).toDouble()
        }.toFloat()
    }
}