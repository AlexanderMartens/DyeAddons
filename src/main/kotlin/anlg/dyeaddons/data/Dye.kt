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
import anlg.dyeaddons.gui.statistics.CeladonStatistics
import net.minecraft.resources.Identifier

data class Guide(
    val author: String,
    val content: String
)

enum class Dye(
    val color: Int,
    val description : String = "",
    val calculator : ((x : Int, y : Int, width : Int, height : Int) -> AbstractCalculator)? = null,
    val statistics : ((x : Int, y : Int, width : Int, height : Int) -> AbstractStatistics)? = null
) {
    AQUAMARINE(
        0x7FFFD4,
        "Drops from Water Sea Creatures",
        ::AquamarineCalculator),
    ARCHFIEND(
        0xB80036,
        "Drops from rolling Archfiend Dice",
        ::ArchfiendCalculator),
    BINGO_BLUE(
        0x002FA7,
        "Purchased from the Bingo Shop"),
    BONE(
        0xE3DAC9,
        "Drops from Skeletons",
        ::BoneCalculator),
    BRICK_RED(
        0xCB4154,
        "Drops from Tarantula Broodfather",
        ::BrickRedCalculator),
    BYZANTIUM(
        0x702963,
        "Drops from Voidgloom Seraph",
        ::ByzantiumCalculator),
    CARMINE(
        0x960018,
        "Drops from Lava Sea Creatures",
        ::CarmineCalculator),
    CELADON(
        0xACE1AF,
        "Drops from Bacte or Blobbercysts",
        ::CeladonCalculator,
        ::CeladonStatistics),
    CELESTE(
        0xB2FFFF,
        "Drops from Sven Packmaster",
        ::CelesteCalculator),
    CHOCOLATE(
        0x7B3F00,
        "Purchased from Chocolate Shop"),
    COPPER(
        0xB87333,
        "Appears as a reward from Garden Visitors",
        ::CopperCalculator),
    CYCLAMEN(
        0xF56FA1,
        "Drops from Mobs in the Crimson Isle",
        ::CyclamenCalculator),
    DARK_PURPLE(
        0x301934,
        "Appears in the Dark Auction"),
    DUNG(
        0x4F2A2A,
        "Drops from Pests",
        ::DungCalculator),
    EMERALD(
        0x50C878,
        "Drops from mining Emeralds",
        ::EmeraldCalculator),
    FLAME(
        0xE25822,
        "Drops from Inferno Demonlord",
        ::FlameCalculator),
    FOSSIL(
        0x866F12,
        "Appears in Fossil Excavator",
        ::FossilCalculator),
    FROSTBITTEN(
        0x09D8EB,
        "Drops from Frozen Corpses",
        ::FrostbittenCalculator),
    HOLLY(
        0x3C6746,
        "Drops from Red Gifts",
        ::HollyCalculator),
    ICEBERG(
        0x71A6D2,
        "Drops from Winter Sea Creatures",
        ::IcebergCalculator),
    JADE(
        0x00A86B,
        "Drops from Crystal Nucleus Bundle",
        ::JadeCalculator),
    LIVID(
        0xCEB7AA,
        "Appears in a Bedrock Chest in Master Mode Floor V",
        ::LividCalculator),
    MANGO(
        0xFDBE02,
        "Drops from breaking any log on a public island",
        ::MangoCalculator),
    MATCHA(
        0x74A12E,
        "Drops from Revenant Horror",
        ::MatchaCalculator
    ),
    MIDNIGHT(
        0x50216C,
    "Drops from Spooky Mobs or Spooky Sea Creatures",
        ::MidnightCalculator),
    MOCHA(
        0x967969,
        "Drops from brewing Potions",
        ::MochaCalculator),
    MYTHOLOGICAL(
        0x6F6F0C,
        "Drops from Mythological Creatures",
        ::MythologicalCalculator),
    NADESHIKO(
        0xF6ADC6,
        "Appears in Superpairs Experiment"),
    NECRON(
        0xE7413C,
        "Appears in a Bedrock Chest in Master Mode Floor VII",
        ::NecronCalculator),
    NYANZA(
        0xE9FFDB,
        "Appears as a reward from Mining Commissions",
        ::NyanzaCalculator
    ),
    PEARLESCENT(
        0x115555,
        "Drops from Mobs in the End",
        ::PearlescentCalculator),
    PELT(
        0x50414C,
        "Drops from Trapper Animals",
        ::PeltCalculator),
    PERIWINKLE(
        0xCCCCFF,
        "Drops from Runic Mobs",
        ::PeriwinkleCalculator),
    PURE_BLACK(
        0x000000,
        "Purchased from Bits Shop"),
    PURE_BLUE(
        0x0013FF,
        "Obtained from the Raffle of the Century"),
    PURE_WHITE(
        0xFFFFFF,
        "Purchased from Bits Shop"),
    PURE_YELLOW(
        0xFFF700,
        "Obtained from the Raffle of the Century"),
    SANGRIA(
        0xD40808,
        "Drops from Riftstalker Bloodfiend",
        ::SangriaCalculator),
    SECRET(
        0x7D7D7D,
        "Drops from Secrets in the Catacombs",
        ::SecretCalculator),
    TENTACLE(
        0x324D6C,
        "Appears in Kuudra Loot Chests",
        ::TentacleCalculator),
    TREASURE(
        0xFCD12A,
        "Drops from Fishing Treasure",
        ::TreasureCalculator),
    WILD_STRAWBERRY(
        0xFF43A4,
        "Drops from harvesting Crops",
        ::WildStrawberryCalculator);

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
}