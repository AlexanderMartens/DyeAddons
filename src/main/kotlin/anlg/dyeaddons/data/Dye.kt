package anlg.dyeaddons.data

import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.gui.calculators.AbstractCalculator
import anlg.dyeaddons.gui.calculators.NyanzaCalculator
import net.minecraft.resources.Identifier

data class Guide(
    val author: String,
    val content: String
)

enum class Dye(
    val color: Int,
    val description : String = "",
    val calculator : ((x : Int, y : Int, width : Int, height : Int) -> AbstractCalculator)?,
) {
    AQUAMARINE(
        0x7FFFD4,
        "Drops from Water Sea Creatures",
        null),
    ARCHFIEND(
        0xB80036,
        "Drops from rolling Archfiend Dice",
        null),
    BINGO_BLUE(
        0x002FA7,
        "Purchased from the Bingo Shop",
        null),
    BONE(
        0xE3DAC9,
        "Drops from Skeletons",
        null),
    BRICK_RED(
        0xCB4154,
        "Drops from Tarantula Broodfather",
        null),
    BYZANTIUM(
        0x702963,
        "Drops from Voidgloom Seraph",
        null),
    CARMINE(
        0x960018,
        "Drops from Lava Sea Creatures",
        null),
    CELADON(
        0xACE1AF,
        "Drops from Bacte or Blobbercysts",
        null),
    CELESTE(
        0xB2FFFF,
        "Drops from Sven Packmaster",
        null),
    CHOCOLATE(
        0x7B3F00,
        "Purchased from Chocolate Shop",
        null),
    COPPER(
        0xB87333,
        "Appears as a reward from Garden Visitors",
        null),
    CYCLAMEN(
        0xF56FA1,
        "Drops from Mobs in the Crimson Isle",
        null),
    DARK_PURPLE(
        0x301934,
        "Appears in the Dark Auction",
        null),
    DUNG(
        0x4F2A2A,
        "Drops from Pests",
        null),
    EMERALD(
        0x50C878,
        "Drops from mining Emeralds",
        null),
    FLAME(
        0xE25822,
        "Drops from Inferno Demonlord",
        null),
    FOSSIL(
        0x866F12,
        "Appears in Fossil Excavator",
        null),
    FROSTBITTEN(
        0x09D8EB,
        "Drops from Frozen Corpses",
        null),
    HOLLY(
        0x3C6746,
        "Drops from Red Gifts",
        null),
    ICEBERG(
        0x71A6D2,
        "Drops from Winter Sea Creatures",
        null),
    JADE(
        0x00A86B,
        "Drops from Crystal Nucleus Bundle",
        null),
    LIVID(
        0xCEB7AA,
        "Appears in a Bedrock Chest in Master Mode Floor V",
        null),
    MANGO(
        0xFDBE02,
        "Drops from breaking any log on a public island",
        null),
    MATCHA(
        0x74A12E,
        "Drops from Revenant Horror",
        null),
    MIDNIGHT(
        0x50216C,
    "Drops from Spooky Mobs or Spooky Sea Creatures",
        null),
    MOCHA(
        0x967969,
        "Drops from brewing Potions",
        null),
    MYTHOLOGICAL(
        0x6F6F0C,
        "Drops from Mythological Creatures",
        null),
    NADESHIKO(
        0xF6ADC6,
        "Appears in Superpairs Experiment",
        null),
    NECRON(
        0xE7413C,
        "Appears in a Bedrock Chest in Master Mode Floor VII",
        null),
    NYANZA(
        0xE9FFDB,
        "Appears as a reward from Mining Commissions",
        ::NyanzaCalculator
    ),
    PEARLESCENT(
        0x115555,
        "Drops from Mobs in the End",
        null),
    PELT(
        0x50414C,
        "Drops from Trapper Animals",
        null),
    PERIWINKLE(
        0xCCCCFF,
        "Drops from Runic Mobs",
        null),
    PURE_BLACK(
        0x000000,
        "Purchased from Bits Shop",
        null),
    PURE_BLUE(
        0x0013FF,
        "Obtained from the Raffle of the Century",
        null),
    PURE_WHITE(
        0xFFFFFF,
        "Purchased from Bits Shop",
        null),
    PURE_YELLOW(
        0xFFF700,
        "Obtained from the Raffle of the Century",
        null),
    SANGRIA(
        0xD40808,
        "Drops from Riftstalker Bloodfiend",
        null),
    SECRET(
        0x7D7D7D,
        "Drops from Secrets in the Catacombs",
        null),
    TENTACLE(
        0x324D6C,
        "Appears in Kuudra Loot Chests",
        null),
    TREASURE(
        0xFCD12A,
        "Drops from Fishing Treasure",
        null),
    WILD_STRAWBERRY(
        0xFF43A4,
        "Drops from harvesting Crops",
        null);

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