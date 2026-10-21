package anlg.dyeaddons.data

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.gui.calculators.*
import anlg.dyeaddons.gui.statistics.*
import anlg.dyeaddons.settings.categories.DebugCategories
import net.minecraft.resources.Identifier

data class Guide(
    val author: String,
    val content: String
)

enum class Dye(
    val color: Int,
    val description : String = "",
    val dropRates : Map<String, Int> = mapOf(),
    val calculator : ((x : Int, y : Int, width : Int, height : Int) -> AbstractCalculator)? = null,
    val statistics : ((x : Int, y : Int, width : Int, height : Int) -> AbstractStatistics)? = null,
    val apiTooltip : String = "",
    val colorCode : ColorCodes,
) {
    AQUAMARINE(
        0x7FFFD4,
        "Drops from Water Sea Creatures",
        mapOf(
            "Common" to 10_000_000,
            "Uncommon" to 5_000_000,
            "Rare" to 2_500_000,
            "Epic" to 500_000,
            "Legendary" to 50_000,
            "Mythic" to 25_000,
        ),
        ::AquamarineCalculator,
        ::AquamarineStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.AQUA),
    ARCHFIEND(
        0xB80036,
        "Drops from rolling Archfiend Dice",
        mapOf(
            "Archfiend" to 6_666,
            "High Class" to 666,
        ),
        ::ArchfiendCalculator,
        ::ArchfiendStatistics,
        "No statistics available in API",
        ColorCodes.DARK_RED),
    BINGO_BLUE(
        0x002FA7,
        "Purchased from the Bingo Shop",
        mapOf(
            "Bingo Points" to 500,
        ),
        statistics = ::BingoBlueStatistics,
        apiTooltip = "Open Bingo Menu to grab Bingo Points",
        colorCode = ColorCodes.DARK_BLUE),
    BONE(
        0xE3DAC9,
        "Drops from Skeletons",
        mapOf(
            "Skeleton" to 3_000_000,
        ),
        ::BoneCalculator,
        ::BoneStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.WHITE),
    BRICK_RED(
        0xCB4154,
        "Drops from Tarantula Broodfather",
        mapOf(
            "T1" to 2_500_000,
            "T2" to 1_000_000,
            "T3" to 500_000,
            "T4" to 250_000,
            "T5" to 100_000,
        ),
        ::BrickRedCalculator,
        ::BrickRedStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.RED),
    BYZANTIUM(
        0x702963,
        "Drops from Voidgloom Seraph",
        mapOf(
            "T1" to 2_500_000,
            "T2" to 1_000_000,
            "T3" to 500_000,
            "T4" to 250_000,
        ),
        ::ByzantiumCalculator,
        ::ByzantiumStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.DARK_PURPLE),
    CARMINE(
        0x960018,
        "Drops from Lava Sea Creatures",
        mapOf(
            "Common" to 10_000_000,
            "Uncommon" to 5_000_000,
            "Rare" to 2_500_000,
            "Epic" to 500_000,
            "Legendary" to 50_000,
            "Mythic" to 25_000,
        ),
        ::CarmineCalculator,
        ::CarmineStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.DARK_RED),
    CELADON(
        0xACE1AF,
        "Drops from Bacte or Blobbercysts",
        mapOf(
            "Blobbercyst" to 25_000,
            "Bacte" to 1_000,
            "Leech Supreme" to 2_500,
        ),
        ::CeladonCalculator,
        ::CeladonStatistics,
        "Uses kill data for blobbercysts and rift data for bactes defeated in the Hypixel API",
        ColorCodes.GREEN),
    CELESTE(
        0xB2FFFF,
        "Drops from Sven Packmaster",
        mapOf(
            "T1" to 2_500_000,
            "T2" to 1_000_000,
            "T3" to 500_000,
            "T4" to 250_000,
        ),
        ::CelesteCalculator,
        ::CelesteStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.AQUA),
    CHOCOLATE(
        0x7B3F00,
        "Purchased from Chocolate Shop",
        statistics = ::ChocolateStatistics,
        apiTooltip = "Uses event data in Hypixel API",
        colorCode = ColorCodes.GOLD),
    COPPER(
        0xB87333,
        "Appears as a reward from Garden Visitors",
        mapOf(
            "Uncommon" to 100_000,
            "Rare" to 50_000,
            "Legendary" to 25_000,
            "Mythic" to 5_000,
            "Special" to 500,
            "Charm" to 3,
        ),
        ::CopperCalculator,
        ::CopperStatistics,
        "Look through the visitor logbook in the garden for visitor data, then click this button",
        ColorCodes.DARK_GRAY),
    CYCLAMEN(
        0xF56FA1,
        "Drops from Mobs in the Crimson Isle",
        mapOf(
            "T1" to 10_000_000,
            "T2" to 2_500_000,
            "T3" to 250_000,
        ),
        ::CyclamenCalculator,
        ::CyclamenStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.RED),
    DARK_PURPLE(
        0x301934,
        "Appears in the Dark Auction",
        mapOf(
            "Dark Auction" to 400,
        ),
        statistics = ::DarkPurpleStatistics,
        apiTooltip = "No statistics available in API",
        colorCode = ColorCodes.DARK_PURPLE),
    DUNG(
        0x4F2A2A,
        "Drops from Pests",
        mapOf(
            "Pest" to 250_000,
            "Elusive" to 50_000,
        ),
        ::DungCalculator,
        ::DungStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.DARK_GRAY),
    EMERALD(
        0x50C878,
        "Drops from Critter Safari critters",
        mapOf(
            "Common" to 500_000,
            "Uncommon" to 250_000,
            "Rare" to 100_000,
            "Epic" to 50_000,
            "Legendary" to 25_000,
            "Sparkling" to 100,
        ),
        ::EmeraldCalculator,
        ::EmeraldStatistics,
        "Uses emerald collection in Hypixel API, assumes 2000 mining fortune. You should remove emeralds mined after Torrhus Canyon update. Uses bestiary for critters. Add 99 for every shiny critter hunted.",
        ColorCodes.DARK_GREEN),
    FLAME(
        0xE25822,
        "Drops from Inferno Demonlord",
        mapOf(
            "T1" to 2_500_000,
            "T2" to 1_000_000,
            "T3" to 500_000,
            "T4" to 250_000,
        ),
        ::FlameCalculator,
        ::FlameStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.GOLD),
    FOSSIL(
        0x866F12,
        "Appears in Fossil Excavator",
        mapOf(
            "Excavator" to 250_000,
        ),
        ::FossilCalculator,
        ::FossilStatistics,
        "Uses attribute data for Perhistorian in Hypixel API, other statistics unavailable",
        ColorCodes.DARK_GRAY),
    FROSTBITTEN(
        0x09D8EB,
        "Drops from Frozen Corpses",
        mapOf(
            "Lapis" to 250_000,
            "Umber" to 25_000,
            "Tungsten" to 25_000,
            "Umber/Tungsten" to 25_000,
            "Vanguard" to 10_000,
        ),
        ::FrostbittenCalculator,
        ::FrostbittenStatistics,
        "Uses frozen corpses looted in Hypixel API",
        ColorCodes.DARK_AQUA),
    HOLLY(
        0x3C6746,
        "Drops from Red Gifts",
        mapOf(
            "Red Gift" to 8_000,
        ),
        ::HollyCalculator,
        ::HollyStatistics,
        "No statistics available in API",
        ColorCodes.DARK_GREEN),
    ICEBERG(
        0x71A6D2,
        "Drops from Winter Sea Creatures",
        mapOf(
            "Common" to 2_500_000,
            "Uncommon" to 1_000_000,
            "Rare" to 500_000,
            "Epic" to 100_000,
            "Legendary" to 25_000,
            "Mythic" to 10_000,
        ),
        ::IcebergCalculator,
        ::IcebergStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.DARK_AQUA),
    JADE(
        0x00A86B,
        "Drops from Crystal Nucleus Bundle",
        mapOf(
            "Nucleus" to 100_000,
        ),
        ::JadeCalculator,
        ::JadeStatistics,
        "Looks at jade crystals placed in Hypixel API",
        ColorCodes.DARK_GREEN),
    LIVID(
        0xCEB7AA,
        "Appears in a Bedrock Chest in Master Mode Floor V",
        mapOf(
            "M5" to 5_000,
        ),
        ::LividCalculator,
        ::LividStatistics,
        "Uses master mode floor 5 completions in Hypixel API",
        ColorCodes.GRAY),
    MANGO(
        0xFDBE02,
        "Drops from tree gifts",
        mapOf(
            "Fig" to 1_000_000,
            "Mangrove" to 500_000,
            "Helix" to 100_000,
        ),
        ::MangoCalculator,
        ::MangoStatistics,
        "Uses tree gifts in Hypixel API",
        ColorCodes.GOLD),
    MATCHA(
        0x74A12E,
        "Drops from Revenant Horror",
        mapOf(
            "T1" to 2_500_000,
            "T2" to 1_000_000,
            "T3" to 500_000,
            "T4" to 250_000,
            "T5" to 100_000,
        ),
        ::MatchaCalculator,
        ::MatchaStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.DARK_GREEN),
    MIDNIGHT(
        0x50216C,
    "Drops from Spooky Mobs or Spooky Sea Creatures",
        mapOf(
            "Common" to 1_000_000,
            "Uncommon" to 1_000_000,
            "Rare" to 1_000_000,
            "Epic" to 50_000,
            "Legendary" to 50_000,
            "Mythic" to 25_000,
            "Spooky" to 250_000,
            "Bat" to 25_000,
            "Horseman" to 25_000,
        ),
        ::MidnightCalculator,
        ::MidnightStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.DARK_PURPLE),
    MOCHA(
        0x967969,
        "Drops from brewing Potions",
        mapOf(
            "T1" to 100_000_000,
            "T2" to 5_000_000,
            "T3" to 2_500_000,
            "T4" to 1_000_000,
            "T5" to 750_000,
            "T6" to 500_000,
            "T7" to 250_000,
            "T8" to 100_000,
        ),
        ::MochaCalculator,
        ::MochaStatistics,
        "No statistics available in API",
        ColorCodes.DARK_GRAY),
    MYTHOLOGICAL(
        0x6F6F0C,
        "Drops from Mythological Creatures",
        mapOf(
            "Common" to 1_000_000,
            "Uncommon" to 1_000_000,
            "Rare" to 500_000,
            "Epic" to 250_000,
            "Legendary" to 50_000,
            "Mythic" to 10_000,
        ),
        ::MythologicalCalculator,
        ::MythologicalStatistics,
        "Uses kill data in Hypixel API, counts kills before Diana v2",
        ColorCodes.DARK_GREEN),
    NADESHIKO(
        0xF6ADC6,
        "Appears in Superpairs Experiment",
        mapOf(
            "Supreme" to 30_000,
            "Transcendent" to 20_000,
            "Metaphysical" to 10_000,
        ),
        ::NadeshikoCalculator,
        ::NadeshikoStatistics,
        "Uses experimentation data in Hypixel API",
        ColorCodes.LIGHT_PURPLE),
    NECRON(
        0xE7413C,
        "Appears in a Bedrock Chest in Master Mode Floor VII",
        mapOf(
            "M7" to 2_500,
        ),
        ::NecronCalculator,
        ::NecronStatistics,
        "Uses master mode floor 5 completions in Hypixel API",
        ColorCodes.GOLD),
    NYANZA(
        0xE9FFDB,
        "Appears as a reward from Mining Commissions",
        mapOf(
            "Commission" to 100_000,
        ),
        ::NyanzaCalculator,
        ::NyanzaStatistics,
        "Look at commission milestones",
        ColorCodes.GREEN),
    PEARLESCENT(
        0x115555,
        "Drops from Mobs in the End",
        mapOf(
            "T1" to 10_000_000,
            "T2" to 5_000_000,
            "T3" to 100_000,
            "Superior" to 25_000,
        ),
        ::PearlescentCalculator,
        ::PearlescentStatistics,
        "Uses kill data in Hypixel API",
        ColorCodes.DARK_GREEN),
    PELT(
        0x50414C,
        "Drops from Trapper Animals",
        mapOf(
            "Trackable" to 50_000,
            "Untrackable" to 25_000,
            "Undetected" to 10_000,
            "Endangered" to 5_000,
            "Elusive" to 2_500,
        ),
        ::PeltCalculator,
        ::PeltStatistics,
        "Uses kill data in Hypixel API, approximates number of kills for each rarity",
        ColorCodes.GRAY),
    PERIWINKLE(
        0xCCCCFF,
        "Drops from Runic Mobs",
        mapOf(
            "T1" to 400_000,
            "T2" to 200_000,
            "T3" to 100_000,
            "T4" to 50_000,
            "T5" to 25_000,
        ),
        ::PeriwinkleCalculator,
        ::PeriwinkleStatistics,
        "Cannot reliably estimate using API",
        ColorCodes.DARK_AQUA),
    PURE_BLACK(
        0x000000,
        "Purchased from Bits Shop",
        mapOf(
            "Bits" to 250_000,
        ),
        statistics = ::PureBlackStatistics,
        apiTooltip = "Uses scoreboard for Bits",
        colorCode = ColorCodes.BLACK),
    PURE_BLUE(
        0x0013FF,
        "Obtained from the Raffle of the Century",
        colorCode = ColorCodes.DARK_BLUE),
    PURE_WHITE(
        0xFFFFFF,
        "Purchased from Bits Shop",
        mapOf(
            "Bits" to 250_000,
        ),
        statistics = ::PureWhiteStatistics,
        apiTooltip = "Uses scoreboard for Bits",
        colorCode = ColorCodes.WHITE),
    PURE_YELLOW(
        0xFFF700,
        "Obtained from the Raffle of the Century",
        colorCode = ColorCodes.YELLOW),
    SANGRIA(
        0xD40808,
        "Drops from Riftstalker Bloodfiend",
        mapOf(
            "T1" to 25_000,
            "T2" to 20_000,
            "T3" to 15_000,
            "T4" to 10_000,
            "T5" to 5_000,
        ),
        ::SangriaCalculator,
        ::SangriaStatistics,
        "Uses slayer boss completions in Hypixel API",
        ColorCodes.DARK_RED),
    SECRET(
        0x7D7D7D,
        "Drops from Secrets in the Catacombs",
        mapOf(
            "Secret" to 250_000,
        ),
        ::SecretCalculator,
        ::SecretStatistics,
        "Uses secret count in Hypixel API",
        ColorCodes.GRAY),
    TENTACLE(
        0x324D6C,
        "Appears in Kuudra Loot Chests",
        mapOf(
            "Basic" to 50_000,
            "Hot" to 40_000,
            "Burning" to 30_000,
            "Fiery" to 20_000,
            "Infernal" to 10_000,
        ),
        ::TentacleCalculator,
        ::TentacleStatistics,
        "Uses kuudra completions in Hypixel API",
        ColorCodes.DARK_PURPLE),
    TREASURE(
        0xFCD12A,
        "Drops from Fishing Treasure",
        mapOf(
            "Good" to 1_000_000,
            "Great" to 100_000,
            "Outstanding" to 10_000,
        ),
        ::TreasureCalculator,
        ::TreasureStatistics,
        "Uses treasure data in Hypixel API",
        ColorCodes.GOLD),
    WILD_STRAWBERRY(
        0xFF43A4,
        "Drops from harvesting Crops",
        mapOf(
            "Crop" to 150_000_000,
            "Vincent" to 2_500,
            "Charm" to 3,
        ),
        ::WildStrawberryCalculator,
        ::WildStrawberryStatistics,
        "Open visitor logbook in the garden for Vincent visits, crop blocks broken not available",
        ColorCodes.LIGHT_PURPLE),;

    fun getTexture(): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, "dyes/${name.lowercase()}.png")
    }

    fun getGuide() : Guide {

        val id = Identifier.fromNamespaceAndPath(MOD_ID, "guides/${name.lowercase()}.txt")

        var author = "Unknown"

        var content = "Failed to Load"

        mc.resourceManager.getResource(id).ifPresent { resource ->
            resource.openAsReader().use { reader ->

                val lines = reader.readLines()

                if (lines.isEmpty()) {
                    content = ""
                    return@ifPresent
                }

                // Extract author
                val authorLine = lines.first()

                author = authorLine
                    .removePrefix("Author:")
                    .trim()

                // Remaining lines become content
                content = lines
                    .drop(1)
                    .joinToString("\n")
                    .trim()
            }
        }

        return Guide(author, content)
    }

    fun getDropRate(dropRate: String, default: Double = 0.0): Double {
        return dropRates[dropRate]?.toDouble() ?: run {
            DyeAddons.debug("Could not find drop rate for $dropRate for $this Dye", DebugCategories.ERROR)
            default
        }
    }

    /**
     * Returns whether the dye is a shop dye (chocolate, pure white, pure black, bingo blue)
     */
    fun isShopDye(): Boolean {
        return this in listOf(CHOCOLATE, PURE_WHITE, PURE_BLACK, BINGO_BLUE)
    }

    override fun toString(): String {
        return this.name
            .lowercase()
            .split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }

    companion object {
        fun normalizeDyeName(name: String): String {
            return name
                .trim()
                .removeSuffix(" Dye")
                .replace(Regex(" "), "_")
                .uppercase()
        }

        fun fromValue(value: String): Dye? {
            return try {
                Dye.valueOf(normalizeDyeName(value))
            } catch (@Suppress("UNUSED_PARAMETER") e: IllegalArgumentException) {
                return null
            }
        }
    }
}