package anlg.dyeaddons.config

enum class VisitorRarity {
    UNCOMMON,
    RARE,
    LEGENDARY,
    MYTHIC,
    SPECIAL
}

data class VisitorData (
    val name : String,
    val rarity : VisitorRarity,
    val visits : Int,
)