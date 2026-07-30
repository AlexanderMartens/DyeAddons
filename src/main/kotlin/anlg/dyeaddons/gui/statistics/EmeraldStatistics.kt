package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfBestiaryKills
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class EmeraldStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Emerald Dye"),
    listOf(
        StatisticField("Emerald Blocks Mined", Parsers.INT),
        StatisticField("Common Safari Critters Hunted", Parsers.INT),
        StatisticField("Uncommon Safari Critters Hunted", Parsers.INT),
        StatisticField("Rare Safari Critters Hunted", Parsers.INT),
        StatisticField("Epic Safari Critters Hunted", Parsers.INT),
        StatisticField("Legendary Safari Critters Hunted", Parsers.INT)),
    Dye.EMERALD
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)

        val emeraldCollection = profileStats?.collection["EMERALD"] ?: 0
        // TODO: Fix this when they show up in the API
        val commonCritters = profileStats?.sumOfBestiaryKills(listOf(
            "strongarm",
            "tepid",
            "foxtrot",
            "cavernfish",
            "flitter",
            "shyworm",

        )) ?: 0
        val uncommonCritters = profileStats?.sumOfBestiaryKills(listOf(
            "polaris",
            "shuddersquid",
            "bluebird",
            "honeybug",
            "treefrog",
            "woodchucker",
            "bloodbat",
            "areita",
            "duplico",
            "gazer",
            "litterbug",
            "solsnatcher",
            "driftling",
        )) ?: 0
        val rareCritters = profileStats?.sumOfBestiaryKills(listOf(
            "billygoat",
            "mantis Shrimp",
            "nozzlenose",
            "troodon",
            "fluffling",
            "hideonfloor",
            "parakeet",
            "gimmiegold",
            "hideonwall",
            "hideyho",
            "chuckwalla",
            "rockmite",
            "scrappy",
            "snoozle",
        )) ?: 0
        val epicCritters = profileStats?.sumOfBestiaryKills(listOf(
            "gemzie",
        )) ?: 0
        val legendaryCritters = profileStats?.sumOfBestiaryKills(listOf(
            "wumpa",
            "doomspiral",
            "macaw"
        )) ?: 0

        val blocksMined = ((emeraldCollection / 5.0 ) / (1.0 + 2_000 / 100.0)).toInt() // Assuming 2000 Mining Fortune

        (this.widgets["Emerald Blocks Mined"]?.widget as EditBox).value = blocksMined.toString()
        (this.widgets["Common Safari Critters Hunted"]?.widget as EditBox).value = commonCritters.toString()
        (this.widgets["Uncommon Safari Critters Hunted"]?.widget as EditBox).value = uncommonCritters.toString()
        (this.widgets["Rare Safari Critters Hunted"]?.widget as EditBox).value = rareCritters.toString()
        (this.widgets["Epic Safari Critters Hunted"]?.widget as EditBox).value = epicCritters.toString()
        (this.widgets["Legendary Safari Critters Hunted"]?.widget as EditBox).value = legendaryCritters.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val blocksMined = context.getInt("Emerald Blocks Mined")
        val commonCritters = context.getInt("Common Safari Critters Hunted")
        val uncommonCritters = context.getInt("Uncommon Safari Critters Hunted")
        val rareCritters = context.getInt("Rare Safari Critters Hunted")
        val epicCritters = context.getInt("Epic Safari Critters Hunted")
        val legendaryCritters = context.getInt("Legendary Safari Critters Hunted")

        val result = blocksMined / 5_000_000.0 +
                commonCritters / 500_000.0 +
                uncommonCritters / 250_000.0 +
                rareCritters / 100_000 +
                epicCritters / 50_000 +
                legendaryCritters / 25_000
        return result
    }
}