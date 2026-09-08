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

class AquamarineCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Aquamarine Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Island/Area" to DropDownCalcWidget(x, y, width, 25, Component.literal("Island/Area"),
            listOf("Normal", "Bayou", "Jerry", "Lotus Atoll", "Moonglade Marsh", "Torrhus Canyon",
            "Oasis", "Goblin Holdout", "Jungle", "Mithril Deposits", "Precursor Remnants", "Abandoned Quarry")),
        "Catches per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Catches per hour"), Parsers.FLOAT),
        "Double Hook Chance" to EditTextCalcWidget(x, y, width, 25, Component.literal("Double Hook Chance"), Parsers.FLOAT),
        "Magic Find" to EditTextCalcWidget(x, y, width, 25, Component.literal("Magic Find"), Parsers.FLOAT),
        "Looting" to EditTextCalcWidget(x, y, width, 25, Component.literal("Looting"), Parsers.INT),
        "Tracking" to EditTextCalcWidget(x, y, width, 25, Component.literal("Tracking"), Parsers.FLOAT),
        "Bloodshot" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Bloodshot")),
        "Bait" to DropDownCalcWidget(x, y, width, 25, Component.literal("Bait"),
            listOf("None", "Whale", "Hotspot", "Spooky", "Shark", "Worm", "Carrot")),
        "Hook" to DropDownCalcWidget(x, y, width, 25, Component.literal("Hook"),
            listOf("None", "Hotspot", "Spooky", "Common", "Puddle Jumper")),
        "Pet" to DropDownCalcWidget(x, y, width, 25, Component.literal("Pet"),
            listOf("Other", "Hermit Crab", "Bat", "Megalodon")),
        "Hotspot" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot")),
        "Spooky Festival" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Spooky Festival")),
        "Fishing Festival" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Fishing Festival")),
        "Squid Hat" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Squid Hat")),
        "Hotspot Tonic" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot Tonic")),
        "Chumcap Bucket" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Chumcap Bucket")),
        "Shark Scale Armor Pieces" to EditTextCalcWidget(x, y, width, 25, Component.literal("Shark Scale Armor Pieces"), Parsers.INT),
    )
) {
    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        widgets["Hotspot Tonic"]?.hidden = !CalcContext(widgets).getBoolean("Hotspot")
        widgets["Shark Scale Armor Pieces"]?.hidden = !CalcContext(widgets).getBoolean("Fishing Festival")
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
            "Normal" -> SeaCreatureArea.NORMAL_WATER
            "Bayou" -> SeaCreatureArea.BAYOU
            "Jerry" -> SeaCreatureArea.JERRY
            "Lotus Atoll" -> SeaCreatureArea.LOTUS_ATOLL
            "Moonglade Marsh" -> SeaCreatureArea.MOONGLADE_MARSH
            "Torrhus Canyon" -> SeaCreatureArea.TORRHUS_CANYON
            "Oasis" -> SeaCreatureArea.OASIS
            "Goblin Holdout" -> SeaCreatureArea.GOBLIN_HOLDOUT
            "Jungle" -> SeaCreatureArea.JUNGLE
            "Mithril Deposits" -> SeaCreatureArea.MITHRIL_DEPOSITS
            "Precursor Remnants" -> SeaCreatureArea.PRECURSOR_REMNANTS
            "Abandoned Quarry" -> SeaCreatureArea.ABANDONED_QUARRY
            else -> SeaCreatureArea.NORMAL_WATER
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
            "Spooky" -> FishingBait.SPOOKY
            "Shark" -> FishingBait.SHARK
            "Carrot" -> FishingBait.CARROT
            "Worm" -> FishingBait.WORM
            else -> null
        }
        val hook = when (context.getString("Hook")) {
            "None" -> null
            "Hotspot" -> FishingRodHook.HOTSPOT
            "Spooky" -> FishingRodHook.SPOOKY
            "Common" -> FishingRodHook.COMMON
            "Puddle Jumper" -> FishingRodHook.PUDDLE_JUMPER
            else -> null
        }
        val pet = when (context.getString("Pet")) {
            "Other" -> null
            "Hermit Crab" -> FishingPet.HERMIT_CRAB
            "Bat" -> FishingPet.BAT
            "Megalodon" -> FishingPet.MEGALODON
            else -> null
        }
        val hotspot = context.getBoolean("Hotspot")
        val spooky = context.getBoolean("Spooky Festival")
        val shark = context.getBoolean("Fishing Festival")
        val squidHat = context.getBoolean("Squid Hat")
        val hotspotTonic = context.getBoolean("Hotspot Tonic")
        val chumCap = context.getBoolean("Chumcap Bucket")
        val sharkScale = context.getInt("Shark Scale Armor Pieces")

        val events = mutableListOf<SeaCreatureArea>()
        if (hotspot) events.add(SeaCreatureArea.HOTSPOT)
        if (spooky) events.add(SeaCreatureArea.SPOOKY)
        if (shark) events.add(SeaCreatureArea.SHARK)

        if (catchesPerHour == 0f) {
            return "Invalid Input"
        }

        val result = SeaCreatureTable(SeaCreatureFluid.WATER,
            area,
            events.toList(),
            hook,
            bait,
            pet,
            tracking,
            carnivalPerk = true,
            piperPerk = true,
            squidHat,
            hotspotTonic,
            sharkScale,
            chumCap,
            catchesPerHour,
            doubleHookChance
        ).hoursForDye(Dye.AQUAMARINE, magicFind, looting, bloodshot) / vincent
        return DecimalFormat("#,###.##").format(result) + " hours"
    }
}