package anlg.dyeaddons.data

import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.gui.calculators.AbstractCalculator
import anlg.dyeaddons.gui.calculators.AquamarineCalculator
import anlg.dyeaddons.gui.calculators.ArchfiendCalculator
import anlg.dyeaddons.gui.calculators.BoneCalculator
import anlg.dyeaddons.gui.calculators.BrickRedCalculator
import anlg.dyeaddons.gui.calculators.ByzantiumCalculator
import anlg.dyeaddons.gui.calculators.CarmineCalculator
import anlg.dyeaddons.gui.calculators.CeladonCalculator
import anlg.dyeaddons.gui.calculators.CelesteCalculator
import anlg.dyeaddons.gui.calculators.CopperCalculator
import anlg.dyeaddons.gui.calculators.CyclamenCalculator
import anlg.dyeaddons.gui.calculators.DungCalculator
import anlg.dyeaddons.gui.calculators.EmeraldCalculator
import anlg.dyeaddons.gui.calculators.FlameCalculator
import anlg.dyeaddons.gui.calculators.FossilCalculator
import anlg.dyeaddons.gui.calculators.FrostbittenCalculator
import anlg.dyeaddons.gui.calculators.HollyCalculator
import anlg.dyeaddons.gui.calculators.IcebergCalculator
import anlg.dyeaddons.gui.calculators.JadeCalculator
import anlg.dyeaddons.gui.calculators.LividCalculator
import anlg.dyeaddons.gui.calculators.MangoCalculator
import anlg.dyeaddons.gui.calculators.MatchaCalculator
import anlg.dyeaddons.gui.calculators.MidnightCalculator
import anlg.dyeaddons.gui.calculators.MochaCalculator
import anlg.dyeaddons.gui.calculators.MythologicalCalculator
import anlg.dyeaddons.gui.calculators.NecronCalculator
import anlg.dyeaddons.gui.calculators.NyanzaCalculator
import anlg.dyeaddons.gui.calculators.PearlescentCalculator
import anlg.dyeaddons.gui.calculators.PeltCalculator
import anlg.dyeaddons.gui.calculators.PeriwinkleCalculator
import anlg.dyeaddons.gui.calculators.SangriaCalculator
import anlg.dyeaddons.gui.calculators.SecretCalculator
import anlg.dyeaddons.gui.calculators.TentacleCalculator
import anlg.dyeaddons.gui.calculators.TreasureCalculator
import anlg.dyeaddons.gui.calculators.WildStrawberryCalculator
import anlg.dyeaddons.gui.statistics.AbstractStatistics
import anlg.dyeaddons.gui.statistics.AquamarineStatistics
import anlg.dyeaddons.gui.statistics.ArchfiendStatistics
import anlg.dyeaddons.gui.statistics.BingoBlueStatistics
import anlg.dyeaddons.gui.statistics.BoneStatistics
import anlg.dyeaddons.gui.statistics.BrickRedStatistics
import anlg.dyeaddons.gui.statistics.ByzantiumStatistics
import anlg.dyeaddons.gui.statistics.CarmineStatistics
import anlg.dyeaddons.gui.statistics.CeladonStatistics
import anlg.dyeaddons.gui.statistics.CelesteStatistics
import anlg.dyeaddons.gui.statistics.ChocolateStatistics
import anlg.dyeaddons.gui.statistics.CopperStatistics
import anlg.dyeaddons.gui.statistics.CyclamenStatistics
import anlg.dyeaddons.gui.statistics.DarkPurpleStatistics
import anlg.dyeaddons.gui.statistics.DungStatistics
import anlg.dyeaddons.gui.statistics.EmeraldStatistics
import anlg.dyeaddons.gui.statistics.FlameStatistics
import anlg.dyeaddons.gui.statistics.FossilStatistics
import anlg.dyeaddons.gui.statistics.FrostbittenStatistics
import anlg.dyeaddons.gui.statistics.HollyStatistics
import anlg.dyeaddons.gui.statistics.IcebergStatistics
import anlg.dyeaddons.gui.statistics.JadeStatistics
import anlg.dyeaddons.gui.statistics.LividStatistics
import anlg.dyeaddons.gui.statistics.MangoStatistics
import anlg.dyeaddons.gui.statistics.MatchaStatistics
import anlg.dyeaddons.gui.statistics.MidnightStatistics
import anlg.dyeaddons.gui.statistics.MochaStatistics
import anlg.dyeaddons.gui.statistics.MythologicalStatistics
import anlg.dyeaddons.gui.statistics.NadeshikoStatistics
import anlg.dyeaddons.gui.statistics.NecronStatistics
import anlg.dyeaddons.gui.statistics.NyanzaStatistics
import anlg.dyeaddons.gui.statistics.PearlescentStatistics
import anlg.dyeaddons.gui.statistics.PeltStatistics
import anlg.dyeaddons.gui.statistics.PeriwinkleStatistics
import anlg.dyeaddons.gui.statistics.PureBlackStatistics
import anlg.dyeaddons.gui.statistics.PureWhiteStatistics
import anlg.dyeaddons.gui.statistics.SangriaStatistics
import anlg.dyeaddons.gui.statistics.SecretStatistics
import anlg.dyeaddons.gui.statistics.TentacleStatistics
import anlg.dyeaddons.gui.statistics.TreasureStatistics
import anlg.dyeaddons.gui.statistics.WildStrawberryStatistics
import net.minecraft.resources.Identifier

data class Guide(
    val author: String,
    val content: String
)

enum class Dye(
    val color: Int,
    val description : String = "",
    val calculator : ((x : Int, y : Int, width : Int, height : Int) -> AbstractCalculator)? = null,
    val statistics : ((x : Int, y : Int, width : Int, height : Int) -> AbstractStatistics)? = null,
    val apiTooltip : String = "",
) {
    AQUAMARINE(
        0x7FFFD4,
        "Drops from Water Sea Creatures",
        ::AquamarineCalculator,
        ::AquamarineStatistics,
        "Uses kill data in Hypixel API"),
    ARCHFIEND(
        0xB80036,
        "Drops from rolling Archfiend Dice",
        ::ArchfiendCalculator,
        ::ArchfiendStatistics,
        "No statistics available in API"),
    BINGO_BLUE(
        0x002FA7,
        "Purchased from the Bingo Shop",
        statistics = ::BingoBlueStatistics,
        apiTooltip = "Open Bingo Menu to grab Bingo Points"),
    BONE(
        0xE3DAC9,
        "Drops from Skeletons",
        ::BoneCalculator,
        ::BoneStatistics,
        "Uses kill data in Hypixel API"),
    BRICK_RED(
        0xCB4154,
        "Drops from Tarantula Broodfather",
        ::BrickRedCalculator,
        ::BrickRedStatistics,
        "Uses slayer boss completions in Hypixel API"),
    BYZANTIUM(
        0x702963,
        "Drops from Voidgloom Seraph",
        ::ByzantiumCalculator,
        ::ByzantiumStatistics,
        "Uses slayer boss completions in Hypixel API"),
    CARMINE(
        0x960018,
        "Drops from Lava Sea Creatures",
        ::CarmineCalculator,
        ::CarmineStatistics,
        "Uses kill data in Hypixel API"),
    CELADON(
        0xACE1AF,
        "Drops from Bacte or Blobbercysts",
        ::CeladonCalculator,
        ::CeladonStatistics,
        "Uses kill data for blobbercysts and rift data for bactes defeated in the Hypixel API"),
    CELESTE(
        0xB2FFFF,
        "Drops from Sven Packmaster",
        ::CelesteCalculator,
        ::CelesteStatistics,
        "Uses slayer boss completions in Hypixel API"),
    CHOCOLATE(
        0x7B3F00,
        "Purchased from Chocolate Shop",
        statistics = ::ChocolateStatistics,
        apiTooltip = "Uses event data in Hypixel API"),
    COPPER(
        0xB87333,
        "Appears as a reward from Garden Visitors",
        ::CopperCalculator,
        ::CopperStatistics,
        "Look through the visitor logbook in the garden for visitor data, then click this button"),
    CYCLAMEN(
        0xF56FA1,
        "Drops from Mobs in the Crimson Isle",
        ::CyclamenCalculator,
        ::CyclamenStatistics,
        "Uses kill data in Hypixel API"),
    DARK_PURPLE(
        0x301934,
        "Appears in the Dark Auction",
        statistics = ::DarkPurpleStatistics,
        apiTooltip = "No statistics available in API"),
    DUNG(
        0x4F2A2A,
        "Drops from Pests",
        ::DungCalculator,
        ::DungStatistics,
        "Uses kill data in Hypixel API"),
    EMERALD(
        0x50C878,
        "Drops from mining Emeralds",
        ::EmeraldCalculator,
        ::EmeraldStatistics,
        "Uses emerald collection in Hypixel API, assumes 2000 mining fortune"),
    FLAME(
        0xE25822,
        "Drops from Inferno Demonlord",
        ::FlameCalculator,
        ::FlameStatistics,
        "Uses slayer boss completions in Hypixel API"),
    FOSSIL(
        0x866F12,
        "Appears in Fossil Excavator",
        ::FossilCalculator,
        ::FossilStatistics,
        "Uses attribute data for Perhistorian in Hypixel API, other statistics unavailable"),
    FROSTBITTEN(
        0x09D8EB,
        "Drops from Frozen Corpses",
        ::FrostbittenCalculator,
        ::FrostbittenStatistics,
        "Uses frozen corpses looted in Hypixel API"),
    HOLLY(
        0x3C6746,
        "Drops from Red Gifts",
        ::HollyCalculator,
        ::HollyStatistics,
        "No statistics available in API"),
    ICEBERG(
        0x71A6D2,
        "Drops from Winter Sea Creatures",
        ::IcebergCalculator,
        ::IcebergStatistics,
        "Uses kill data in Hypixel API"),
    JADE(
        0x00A86B,
        "Drops from Crystal Nucleus Bundle",
        ::JadeCalculator,
        ::JadeStatistics,
        "Looks at jade crystals placed in Hypixel API"),
    LIVID(
        0xCEB7AA,
        "Appears in a Bedrock Chest in Master Mode Floor V",
        ::LividCalculator,
        ::LividStatistics,
        "Uses master mode floor 5 completions in Hypixel API"),
    MANGO(
        0xFDBE02,
        "Drops from breaking any log on a public island",
        ::MangoCalculator,
        ::MangoStatistics,
        "Uses foraging collection in Hypixel API, assumes 1500 foraging fortune"),
    MATCHA(
        0x74A12E,
        "Drops from Revenant Horror",
        ::MatchaCalculator,
        ::MatchaStatistics,
        "Uses slayer boss completions in Hypixel API"),
    MIDNIGHT(
        0x50216C,
    "Drops from Spooky Mobs or Spooky Sea Creatures",
        ::MidnightCalculator,
        ::MidnightStatistics,
        "Uses kill data in Hypixel API"),
    MOCHA(
        0x967969,
        "Drops from brewing Potions",
        ::MochaCalculator,
        ::MochaStatistics,
        "No statistics available in API"),
    MYTHOLOGICAL(
        0x6F6F0C,
        "Drops from Mythological Creatures",
        ::MythologicalCalculator,
        ::MythologicalStatistics,
        "Uses kill data in Hypixel API, counts kills before Diana v2"),
    NADESHIKO(
        0xF6ADC6,
        "Appears in Superpairs Experiment",
        statistics = ::NadeshikoStatistics,
        apiTooltip = "Uses experimentation data in Hypixel API"),
    NECRON(
        0xE7413C,
        "Appears in a Bedrock Chest in Master Mode Floor VII",
        ::NecronCalculator,
        ::NecronStatistics,
        "Uses master mode floor 5 completions in Hypixel API"),
    NYANZA(
        0xE9FFDB,
        "Appears as a reward from Mining Commissions",
        ::NyanzaCalculator,
        ::NyanzaStatistics,
        "Look at commission milestones"),
    PEARLESCENT(
        0x115555,
        "Drops from Mobs in the End",
        ::PearlescentCalculator,
        ::PearlescentStatistics,
        "Uses kill data in Hypixel API"),
    PELT(
        0x50414C,
        "Drops from Trapper Animals",
        ::PeltCalculator,
        ::PeltStatistics,
        "Uses kill data in Hypixel API, approximates number of kills for each rarity"),
    PERIWINKLE(
        0xCCCCFF,
        "Drops from Runic Mobs",
        ::PeriwinkleCalculator,
        ::PeriwinkleStatistics,
        "Looks at runebook kills in accessory bag"),
    PURE_BLACK(
        0x000000,
        "Purchased from Bits Shop",
        statistics = ::PureBlackStatistics,
        apiTooltip = "Uses scoreboard for Bits"),
    PURE_BLUE(
        0x0013FF,
        "Obtained from the Raffle of the Century"),
    PURE_WHITE(
        0xFFFFFF,
        "Purchased from Bits Shop",
        statistics = ::PureWhiteStatistics,
        apiTooltip = "Uses scoreboard for Bits"),
    PURE_YELLOW(
        0xFFF700,
        "Obtained from the Raffle of the Century"),
    SANGRIA(
        0xD40808,
        "Drops from Riftstalker Bloodfiend",
        ::SangriaCalculator,
        ::SangriaStatistics,
        "Uses slayer boss completions in Hypixel API"),
    SECRET(
        0x7D7D7D,
        "Drops from Secrets in the Catacombs",
        ::SecretCalculator,
        ::SecretStatistics,
        "Uses secret count in Hypixel API"),
    TENTACLE(
        0x324D6C,
        "Appears in Kuudra Loot Chests",
        ::TentacleCalculator,
        ::TentacleStatistics,
        "Uses kuudra completions in Hypixel API"),
    TREASURE(
        0xFCD12A,
        "Drops from Fishing Treasure",
        ::TreasureCalculator,
        ::TreasureStatistics,
        "Uses treasure data in Hypixel API"),
    WILD_STRAWBERRY(
        0xFF43A4,
        "Drops from harvesting Crops",
        ::WildStrawberryCalculator,
        ::WildStrawberryStatistics,
        "Open visitor logbook in the garden for Vincent visits, crop blocks broken not available"),;

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
                .removeSuffix(" Dye")
                .replace(Regex(" "), "_")
                .uppercase()
                .trim()
        }

        fun fromValue(value: String): Dye {
            return Dye.valueOf(value
                .removeSuffix(" Dye")
                .replace(Regex(" "), "_")
                .uppercase()
                .trim())
        }
    }
}