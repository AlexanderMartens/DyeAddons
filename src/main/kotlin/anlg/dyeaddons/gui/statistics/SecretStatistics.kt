package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.api.ProfileCache
import anlg.dyeaddons.api.getMember
import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class SecretStatistics(
    x: Int,
    y: Int,
    width: Int,
    height: Int
) : AbstractStatistics(
    x,
    y,
    width,
    height,
    Component.literal("Secret Dye"),
    listOf(
        StatisticField("Catacombs Secrets Collected", Parsers.INT),),
    Dye.SECRET
) {
    override fun loadFromApi() {
        val profileStats = ProfileCache.latestProfile?.getMember(mc.player?.uuid)
        val secrets = profileStats?.dungeons?.get("secrets")?.asInt ?: 0

        (this.widgets["Catacombs Secrets Collected"]?.widget as EditBox).value = secrets.toString()
    }

    override fun getProgress(): Double {
        val context = CalcContext(widgets)

        val secrets = context.getInt("Catacombs Secrets Collected")

        val result = secrets / 1_000_000.0
        return result
    }
}