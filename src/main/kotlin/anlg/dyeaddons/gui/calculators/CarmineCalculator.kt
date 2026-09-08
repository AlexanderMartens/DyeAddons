package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.*
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class CarmineCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Carmine Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Island/Area" to DropDownCalcWidget(x, y, width, 25, Component.literal("Island/Area"),
            listOf("Crimson Isle", "Magma Fields", "Precursor Remnants", "Moonglade Marsh")),
        "Catches per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Catches per hour"), Parsers.FLOAT),
        "Double Hook Chance" to EditTextCalcWidget(x, y, width, 25, Component.literal("Double Hook Chance"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Bloodshot" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Bloodshot")),
        "Bait" to DropDownCalcWidget(x, y, width, 25, Component.literal("Bait"),
            listOf("None", "Whale", "Hotspot")),
        "Hook" to DropDownCalcWidget(x, y, width, 25, Component.literal("Hook"),
            listOf("None", "Hotspot", "Common")),
        "Pet" to DropDownCalcWidget(x, y, width, 25, Component.literal("Pet"),
            listOf("Other", "Hermit Crab")),
        "Hotspot" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot")),
        "Hotspot Tonic" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot Tonic")),
    )
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        widgets["Hotspot Tonic"]?.hidden = !CalcContext(widgets).getBoolean("Hotspot")
        super.extractWidgetRenderState(context, mouseX, mouseY, partialTick)
    }

    override fun getOutput(): String {
        val context = CalcContext(widgets)

        val vincent = when(context.getString("Vincent Dye Buff")) {
            "1x" -> 1f
            "2x" -> 2f
            "3x" -> 3f
            else -> 1f
        }
        val area = when (context.getString("Island/Area")) {
            "Crimson Isle" -> SeaCreatureArea.CRIMSON_ISLE
            "Magma Fields" -> SeaCreatureArea.MAGMA_FIELDS
            "Moonglade Marsh" -> SeaCreatureArea.MOONGLADE_MARSH
            "Precursor Remnants" -> SeaCreatureArea.PRECURSOR_REMNANTS
            else -> SeaCreatureArea.CRIMSON_ISLE
        }
        val catchesPerHour = context.getFloat("Catches per hour")
        val doubleHookChance = context.getFloat("Double Hook Chance")
        val magicFind = context.getFloat("Magic Find")
        val looting = context.getInt("Looting")
        val tracking = context.getFloat("Tracking")
        val bloodshot = context.getBoolean("Bloodshot")
        val bait = when (context.getString("Bait")) {
            "None" -> null
            "Whale" -> FishingBait.WHALE
            "Hotspot" -> FishingBait.HOTSPOT
            else -> null
        }
        val hook = when (context.getString("Hook")) {
            "None" -> null
            "Hotspot" -> FishingRodHook.HOTSPOT
            "Common" -> FishingRodHook.COMMON
            else -> null
        }
        val pet = when (context.getString("Pet")) {
            "Other" -> null
            "Hermit Crab" -> FishingPet.HERMIT_CRAB
            else -> null
        }
        val hotspot = context.getBoolean("Hotspot")
        val hotspotTonic = context.getBoolean("Hotspot Tonic")

        val events = mutableListOf<SeaCreatureArea>()
        if (hotspot) events.add(SeaCreatureArea.HOTSPOT)

        if (catchesPerHour == 0f) {
            return "Invalid Input"
        }

        val result = SeaCreatureTable(SeaCreatureFluid.LAVA,
            area,
            events.toList(),
            hook,
            bait,
            pet,
            tracking,
            carnivalPerk = true,
            piperPerk = true,
            squidHat = false,
            hotspotTonic,
            sharkScaleArmor = 0,
            chumcapBucket = false,
            catchesPerHour,
            doubleHookChance
        ).hoursForDye(Dye.CARMINE, magicFind, looting, bloodshot) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}