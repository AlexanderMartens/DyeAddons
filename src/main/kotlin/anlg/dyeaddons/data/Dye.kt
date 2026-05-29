package anlg.dyeaddons.data

import anlg.dyeaddons.DyeAddons.Companion.MOD_ID
import anlg.dyeaddons.DyeAddons.Companion.mc
import net.minecraft.resources.Identifier

data class Guide(
    val author: String,
    val content: String
)

enum class Dye(val color: Int, val description : String = "") {
    AQUAMARINE(
        0x7FFFD4,
        "Drops from Water Sea Creatures"),
    ARCHFIEND(
        0xB80036,
        "Drops from rolling Archfiend Dice"),
    BINGO_BLUE(
        0x002FA7,
        "Purchased from the Bingo Shop"),
    BONE(
        0xE3DAC9,
        "Drops from Skeletons"),
    BRICK_RED(
        0xCB4154,
        "Drops from Tarantula Broodfather"),
    BYZANTIUM(
        0x702963,
        "Drops from Voidgloom Seraph"),
    CARMINE(
        0x960018,
        "Drops from Lava Sea Creatures"),
    CELADON(
        0xACE1AF,
        "Drops from Bacte or Blobbercysts"),
    CELESTE(
        0xB2FFFF,
        "Drops from Sven Packmaster"),
    CHOCOLATE(
        0x7B3F00,
        "Purchased from Chocolate Shop"),
    COPPER(
        0xB87333,
        "Appears as a reward from Garden Visitors"),
    CYCLAMEN(
        0xF56FA1,
        "Drops from Mobs in the Crimson Isle"),
    DARK_PURPLE(
        0x301934,
        "Appears in the Dark Auction"),
    DUNG(
        0x4F2A2A,
        "Drops from Pests"),
    EMERALD(
        0x50C878,
        "Drops from mining Emeralds"),
    FLAME(
        0xE25822,
        "Drops from Inferno Demonlord"),
    FOSSIL(
        0x866F12,
        "Appears in Fossil Excavator"),
    FROSTBITTEN(
        0x09D8EB,
        "Drops from Frozen Corpses"),
    HOLLY(
        0x3C6746,
        "Drops from Red Gifts"),
    ICEBERG(
        0x71A6D2,
        "Drops from Winter Sea Creatures"),
    JADE(
        0x00A86B,
        "Drops from Crystal Nucleus Bundle"),
    LIVID(
        0xCEB7AA,
        "Appears in a Bedrock Chest in Master Mode Floor V"),
    MANGO(
        0xFDBE02,
        "Drops from breaking any log on a public island"),
    MATCHA(
        0x74A12E,
        "Drops from Revenant Horror"),
    MIDNIGHT(
        0x50216C,
    "Drops from Spooky Mobs or Spooky Sea Creatures"),
    MOCHA(
        0x967969,
        "Drops from brewing Potions"),
    MYTHOLOGICAL(
        0x6F6F0C,
        "Drops from Mythological Creatures"),
    NADESHIKO(
        0xF6ADC6,
        "Appears in Superpairs Experiment"),
    NECRON(
        0xE7413C,
        "Appears in a Bedrock Chest in Master Mode Floor VII"),
    NYANZA(
        0xE9FFDB,
        "Appears as a reward from Mining Commissions"),
    PEARLESCENT(
        0x115555,
        "Drops from Mobs in the End"),
    PELT(
        0x50414C,
        "Drops from Trapper Animals"),
    PERIWINKLE(
        0xCCCCFF,
        "Drops from Runic Mobs"),
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
        "Drops from Riftstalker Bloodfiend"),
    SECRET(
        0x7D7D7D,
        "Drops from Secrets in the Catacombs"),
    TENTACLE(
        0x324D6C,
        "Appears in Kuudra Loot Chests"),
    TREASURE(
        0xFCD12A,
        "Drops from Fishing Treasure"),
    WILD_STRAWBERRY(
        0xFF43A4,
        "Drops from harvesting Crops");

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