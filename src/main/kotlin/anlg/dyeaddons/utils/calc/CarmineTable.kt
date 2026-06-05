package anlg.dyeaddons.utils.calc

enum class LavaSeaCreature (val baseChance : Float){
    COMMON(5_000_000f),
    UNCOMMON(5_000_000f),
    RARE(2_500_000f),
    EPIC(2_500_000f),
    LEGENDARY(50_000f),
    MYTHIC(50_000f)
}

data class LavaSeaCreatureItem (
    val name : String,
    val rarity : LavaSeaCreature,
    var weight : Float,
    val hotspot : Boolean
)

class CarmineTable(val hotspot : Float, tracking : Float, whaleBait : Boolean) {

    private val normalTable = listOf(
        LavaSeaCreatureItem("Magma Slug", LavaSeaCreature.UNCOMMON, 1_600f, false),
        LavaSeaCreatureItem("Moogma", LavaSeaCreature.UNCOMMON, 1_200f, false),
        LavaSeaCreatureItem("Lava Leech", LavaSeaCreature.RARE, 600f, false),
        LavaSeaCreatureItem("Pyroclastic Worm", LavaSeaCreature.RARE, 400f, false),
        LavaSeaCreatureItem("Lava Flame", LavaSeaCreature.RARE, 360f, false),
        LavaSeaCreatureItem("Fire Eel", LavaSeaCreature.RARE, 280f, false),
        LavaSeaCreatureItem("Taurus", LavaSeaCreature.EPIC, 160f, false),
        LavaSeaCreatureItem("Thunder", LavaSeaCreature.LEGENDARY, 40f, false),
        LavaSeaCreatureItem("Lord Jawbus", LavaSeaCreature.MYTHIC, 8f, false),
    )

    private val hotspotTable = listOf(
        LavaSeaCreatureItem("Fried Chicken", LavaSeaCreature.COMMON, 5_000f, true),
        LavaSeaCreatureItem("Volcanic Snail", LavaSeaCreature.UNCOMMON, 2_875f, true),
        LavaSeaCreatureItem("Fireproof Witch", LavaSeaCreature.RARE, 1_500f, true),
        LavaSeaCreatureItem("Magma Pillar", LavaSeaCreature.EPIC, 500f, true),
        LavaSeaCreatureItem("Fiery Scuttler", LavaSeaCreature.LEGENDARY, 100f, true),
        LavaSeaCreatureItem("Ragnarok", LavaSeaCreature.MYTHIC, 25f, true),
    )

    init {
        normalTable.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= LavaSeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f)
        }
        hotspotTable.forEach { creature ->
            creature.weight *= (if (creature.weight <= 400f && whaleBait) 1.25f else 1.0f) *
                    (if (creature.rarity >= LavaSeaCreature.LEGENDARY) (1f + tracking / 100f) else 1.0f)
        }
    }

    private fun totalNormalWeight() : Float {
        return normalTable.sumOf { it.weight.toDouble() }.toFloat()
    }

    private fun totalHotspotWeight() : Float {
        return hotspotTable.sumOf { it.weight.toDouble() }.toFloat()
    }

    // I am assuming you roll for the dye for each scuttler
    fun getAverageDropChance() : Float {
        return totalNormalWeight() / normalTable.sumOf { creature ->
            (creature.weight / creature.rarity.baseChance).toDouble()
        }.toFloat() * (1f - hotspot) +
                totalHotspotWeight() / hotspotTable.sumOf { creature ->
            (creature.weight / creature.rarity.baseChance *
                    if (creature.name == "Fiery Scuttler") 6f else 1f).toDouble()
        }.toFloat() * hotspot
    }
}