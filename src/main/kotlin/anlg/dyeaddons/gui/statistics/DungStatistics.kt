package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.api.sumOfKills
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class DungStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Dung Dye"),
    listOf(
        StatisticField("Pest Kills", Parsers.INT),
        StatisticField("Elusive Pest Kills", Parsers.INT),
        StatisticField("Overbloom", Parsers.FLOAT)),
    Dye.DUNG
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val playerStats = profileStats?.playerStats

        val pests = playerStats?.sumOfKills(listOf(
            "pest_mosquito",
            "pest_rat",
            "pest_moth",
            "pest_cricket",
            "pest_worm",
            "pest_mite",
            "pest_locust",
            "pest_fly",
            "pest_slug",
            "pest_bettle",
            "pest_firefly",
            "pest_praying_mantis",
            "pest_dragonfly"
        )) ?: 0
        val elusivePests = playerStats?.sumOfKills(listOf("pest_mouse", "pest_lunar_moth")) ?: 0

        (this.widgets["Pest Kills"]?.widget as EditBox).value = pests.toString()
        (this.widgets["Elusive Pest Kills"]?.widget as EditBox).value = elusivePests.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val pests = context.getInt("Pest Kills")
        val elusivePests = context.getInt("Elusive Pest Kills")
        val overbloom = context.getFloat("Overbloom")

        val result = (pests / 250_000.0 + elusivePests / 50_000.0) *
                (1.0 + overbloom / 100.0)
        return result
    }
}