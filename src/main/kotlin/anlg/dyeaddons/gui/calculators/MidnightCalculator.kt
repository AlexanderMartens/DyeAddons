package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.data.CalcContext
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Parsers
import anlg.dyeaddons.gui.widgets.CheckboxCalcWidget
import anlg.dyeaddons.gui.widgets.DropDownCalcWidget
import anlg.dyeaddons.gui.widgets.EditTextCalcWidget
import anlg.dyeaddons.utils.calc.FishingBait
import anlg.dyeaddons.utils.calc.FishingPet
import anlg.dyeaddons.utils.calc.FishingRodHook
import anlg.dyeaddons.utils.calc.SeaCreatureArea
import anlg.dyeaddons.utils.calc.SeaCreatureFluid
import anlg.dyeaddons.utils.calc.SeaCreatureTable
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

class MidnightCalculator(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    Component.literal("Midnight Dye"),
    mapOf(
        "Vincent Dye Buff" to DropDownCalcWidget(x, y, width, 25, Component.literal("Vincent Dye Buff"), listOf("1x", "2x", "3x")),
        "Fishing" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Fishing"), true),
        "Headless Horseman Kills per hour" to EditTextCalcWidget(x, y, width, 25, Component.literal("Headless Horseman Kills per hour"), Parsers.FLOAT),
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
        "Fishing Festival" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Fishing Festival")),
        "Squid Hat" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Squid Hat")),
        "Hotspot Tonic" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Hotspot Tonic")),
        "Chumcap Bucket" to CheckboxCalcWidget(x, y, width, 25, Component.literal("Chumcap Bucket")),
        "Shark Scale Armor Pieces" to EditTextCalcWidget(x, y, width, 25, Component.literal("Shark Scale Armor Pieces"), Parsers.INT),
    )
) {

    override fun extractWidgetRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (CalcContext(widgets).getBoolean("Fishing")) {
            // Hide horseman widgets, unhide fishing widgets
            widgets["Headless Horseman Kills per hour"]?.hidden = true
            widgets["Catches per hour"]?.hidden = false
            widgets["Double Hook Chance"]?.hidden = false
            widgets["Tracking"]?.hidden = false
            widgets["Bloodshot"]?.hidden = false
            widgets["Bait"]?.hidden = false
            widgets["Hook"]?.hidden = false
            widgets["Pet"]?.hidden = false
            widgets["Hotspot"]?.hidden = false
            widgets["Fishing Festival"]?.hidden = false
            widgets["Squid Hat"]?.hidden = false
            widgets["Hotspot Tonic"]?.hidden = false
            widgets["Chumcap Bucket"]?.hidden = false
            widgets["Shark Scale Armor Pieces"]?.hidden = false
            widgets["Hotspot Tonic"]?.hidden = !CalcContext(widgets).getBoolean("Hotspot")
            widgets["Shark Scale Armor Pieces"]?.hidden = !CalcContext(widgets).getBoolean("Fishing Festival")
        } else {
            widgets["Headless Horseman Kills per hour"]?.hidden = false
            widgets["Catches per hour"]?.hidden = true
            widgets["Double Hook Chance"]?.hidden = true
            widgets["Tracking"]?.hidden = true
            widgets["Bloodshot"]?.hidden = true
            widgets["Bait"]?.hidden = true
            widgets["Hook"]?.hidden = true
            widgets["Pet"]?.hidden = true
            widgets["Hotspot"]?.hidden = true
            widgets["Fishing Festival"]?.hidden = true
            widgets["Squid Hat"]?.hidden = true
            widgets["Hotspot Tonic"]?.hidden = true
            widgets["Chumcap Bucket"]?.hidden = true
            widgets["Shark Scale Armor Pieces"]?.hidden = true
        }
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
        val fishing = context.getBoolean("Fishing")
        val horsemanKillsPerHour = context.getFloat("Headless Horseman Kills per hour")
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
        val shark = context.getBoolean("Fishing Festival")
        val squidHat = context.getBoolean("Squid Hat")
        val hotspotTonic = context.getBoolean("Hotspot Tonic")
        val chumCap = context.getBoolean("Chumcap Bucket")
        val sharkScale = context.getInt("Shark Scale Armor Pieces")

        val events = mutableListOf<SeaCreatureArea>()
        if (hotspot) events.add(SeaCreatureArea.HOTSPOT)
        events.add(SeaCreatureArea.SPOOKY)
        if (shark) events.add(SeaCreatureArea.SHARK)

        if ((fishing && catchesPerHour == 0f) || (!fishing && horsemanKillsPerHour == 0f)) {
            return "Invalid Input"
        }
        val result : Float
        if (fishing) {
            result = SeaCreatureTable(SeaCreatureFluid.WATER,
                SeaCreatureArea.NORMAL_WATER,
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
            ).hoursForDye(Dye.MIDNIGHT, magicFind, looting, bloodshot) / vincent
        } else {
            result = 50_000f / horsemanKillsPerHour / (1f + magicFind / 100f) / (1f + looting * 0.15f) / vincent
        }
        return DecimalFormat("#,###.##").format(result) + " hours" +
                if (!fishing) " and " + DecimalFormat("#,###").format(result * horsemanKillsPerHour) + " Horseman's Candles" else ""
    }
}