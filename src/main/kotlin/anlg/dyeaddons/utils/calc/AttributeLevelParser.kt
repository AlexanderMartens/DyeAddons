package anlg.dyeaddons.utils.calc

enum class AttributeRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY
}
object AttributeLevelParser {
    fun getAttributeLevel(rarity : AttributeRarity, stack : Int) : Int {
        return when (rarity) {
            AttributeRarity.COMMON -> when (stack) {
                0 -> 0
                in 1..3 -> 1
                in 4..8 -> 2
                in 9..14 -> 3
                in 15..21 -> 4
                in 22..29 -> 5
                in 30..39 -> 6
                in 40..53 -> 7
                in 54..71 -> 8
                in 72..95 -> 9
                96 -> 10
                else -> 0
            }
            AttributeRarity.UNCOMMON -> when (stack) {
                0 -> 0
                in 1..2 -> 1
                in 3..5 -> 2
                in 6..9 -> 3
                in 10..14 -> 4
                in 15..20 -> 5
                in 21..27 -> 6
                in 28..35 -> 7
                in 36..47 -> 8
                in 48..63 -> 9
                64 -> 10
                else -> 0
            }
            AttributeRarity.RARE -> when (stack) {
                0 -> 0
                in 1..2 -> 1
                in 3..5 -> 2
                in 6..8 -> 3
                in 9..12 -> 4
                in 13..16 -> 5
                in 17..21 -> 6
                in 22..27 -> 7
                in 28..35 -> 8
                in 36..47 -> 9
                48 -> 10
                else -> 0
            }
            AttributeRarity.EPIC -> when (stack) {
                0 -> 0
                in 1..1 -> 1
                in 2..3 -> 2
                in 4..5 -> 3
                in 6..8 -> 4
                in 9..11 -> 5
                in 12..15 -> 6
                in 16..19 -> 7
                in 20..24 -> 8
                in 25..31 -> 9
                32 -> 10
                else -> 0
            }
            AttributeRarity.LEGENDARY -> when (stack) {
                0 -> 0
                in 1..1 -> 1
                in 2..2 -> 2
                in 3..4 -> 3
                in 5..6 -> 4
                in 7..8 -> 5
                in 9..11 -> 6
                in 12..14 -> 7
                in 15..18 -> 8
                in 19..23 -> 9
                24 -> 10
                else -> 0
            }
        }
    }
}