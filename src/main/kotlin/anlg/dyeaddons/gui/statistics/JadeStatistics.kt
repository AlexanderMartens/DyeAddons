package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.objPath
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.utils.calc.AttributeLevelParser
import anlg.dyeaddons.utils.calc.AttributeRarity
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class JadeStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Jade Dye"),
    listOf(
        StatisticField("Nucleus Runs Completed", Parsers.INT),
        StatisticField("Mole Pet Level", Parsers.INT),
        StatisticField("High Roller Perk", Parsers.BOOL),
        StatisticField("Bigger Box Level", Parsers.INT),
        StatisticField("Echo of Box Level", Parsers.INT),
        StatisticField("Echo of Echo Level", Parsers.INT)),
    Dye.JADE
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val attributes = profileStats?.attributes?.getAsJsonObject("stacks")

        val nucleusRuns = profileStats?.miningCore?.objPath("crystals", "jade_crystal")?.get("total_placed") ?: 0
        val highRoller = profileStats?.playerData?.perks["high_roller"] ?: 0
        val biggerBox = AttributeLevelParser.getAttributeLevel(AttributeRarity.UNCOMMON, attributes?.get("bigger_box")?.asInt ?: 0)
        val echoBox = AttributeLevelParser.getAttributeLevel(AttributeRarity.UNCOMMON, attributes?.get("echo_of_boxes")?.asInt ?: 0)
        val echoEcho = AttributeLevelParser.getAttributeLevel(AttributeRarity.LEGENDARY, attributes?.get("echo_of_echoes")?.asInt ?: 0)

        (this.widgets["Nucleus Runs Completed"]?.widget as EditBox).value = nucleusRuns.toString()
        (this.widgets["High Roller Perk"]?.widget as EditBox).value = highRoller.toString()
        (this.widgets["Bigger Box Level"]?.widget as EditBox).value = biggerBox.toString()
        (this.widgets["Echo of Box Level"]?.widget as EditBox).value = echoBox.toString()
        (this.widgets["Echo of Echo Level"]?.widget as EditBox).value = echoEcho.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val nucleusRuns = context.getInt("Nucleus Runs Completed")
        val molePet = context.getInt("Mole Pet Level")
        val highRoller = context.getBoolean("High Roller Perk")
        val biggerBox = context.getInt("Bigger Box Level")
        val echoBox = context.getInt("Echo of Box Level")
        val echoEcho = context.getInt("Echo of Echo Level")

        var extraItems = 0.0
        extraItems += molePet / 100.0
        if (highRoller) extraItems += 1.0
        extraItems += (biggerBox / 20.0) * (1.0 + (echoBox / 50.0) * (1.0 + (echoEcho / 20.0)))

        val result = nucleusRuns * (17.0 + extraItems) / 500_000.0

        return result
    }
}