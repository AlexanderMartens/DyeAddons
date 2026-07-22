package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.MeterData
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.ScoreboardUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.RngMeter
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

object RngMeterHandler {

    private val METER_PATTERN = Regex("""(?:Stored [\w ]+ (?:XP|Score):| +) ([\d,]+)(?:/[\d,.]+[Mk]?)?""")
    private val SELECTED_PATTERN = Regex("""Selected Drop [\w ]+ Dye""")
    private val SELECTED_CHAT_PATTERN = Regex("""You set your ([\w ()]+) RNG Meter to drop ([\w ]+)!""")
    private val CHAT_METER_RESET_PATTERN = Regex("""You reset your selected drop for your ([\w ()]+) RNG Meter!""")
    private val CHAT_METER = Regex("""RNG Meter - ([\d,]+) Stored XP""")

    private val meterNames = mapOf(
        "Revenant Horror" to "zombie",
        "Tarantula Broodfather" to "spider",
        "Sven Packmaster" to "wolf",
        "Voidgloom Seraph" to "enderman",
        "Riftstalker Bloodfiend" to "vampire",
        "Inferno Demonlord" to "blaze",
        "Catacombs (M5)" to "m5",
        "Catacombs (M7)" to "m7",
        "Experimentation Table" to "experimentation",
        "Crystal Nucleus" to "nucleus",
        "Frozen Corpses" to "frozenCorpse"
    )

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val unformattedText = event.unformattedText.trim()
        if (CHAT_METER_RESET_PATTERN.matches(unformattedText)) {
            val meterType = CHAT_METER_RESET_PATTERN.matchEntire(unformattedText)?.groupValues?.get(1) ?: return
            val meterName = meterNames[meterType] ?: return
            val meters = ProfileStorage.lastPlayedProfile()?.rngMeters ?: return

            meters[meterName]?.selected = false
            DyeAddons.debug("Unselected dye for $meterName meter", DebugCategories.MENU_EVENT)
            return
        }
        if (SELECTED_PATTERN.matches(unformattedText)) {
            val selectedMatch = SELECTED_CHAT_PATTERN.matchEntire(unformattedText) ?: return
            val meterType = selectedMatch.groupValues[1]
            val item = selectedMatch.groupValues[2]

            val meterName = meterNames[meterType] ?: return

            val meters = ProfileStorage.lastPlayedProfile()?.rngMeters ?: return

            meters[meterName]?.selected = item.contains("Dye")
            DyeAddons.debug("Selected dye for $meterName meter to ${item.contains("Dye")}", DebugCategories.MENU_EVENT)
            return
        }
        if (CHAT_METER.matches(unformattedText)) {
            val meter = CHAT_METER.matchEntire(unformattedText)?.groupValues?.get(1)
                ?.replace(",","")
                ?.toIntOrNull() ?: return
            val meterType = when {
                ScoreboardUtils.getLineAfter("Revenant Horror") != "" -> "zombie"
                ScoreboardUtils.getLineAfter("Tarantula Broodfather") != "" -> "spider"
                ScoreboardUtils.getLineAfter("Sven Packmaster") != "" -> "wolf"
                ScoreboardUtils.getLineAfter("Voidgloom Seraph") != "" -> "enderman"
                ScoreboardUtils.getLineAfter("Riftstalker Bloodfiend") != "" -> "vampire"
                ScoreboardUtils.getLineAfter("Inferno Demonlord") != "" -> "blaze"
                else -> return
            }
            ProfileStorage.lastPlayedProfile()?.rngMeters[meterType]?.progress = meter
            DyeAddons.debug("Set meter for $meterType meter to $meter", DebugCategories.MENU_EVENT)
        }
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val title = event.inventoryName

        when (title) {
            "RNG Meter" -> parseRngMenu(event)
            "Slayer RNG Meters" -> parseSlayerRngMenu(event)
            "Catacombs RNG Meters" -> parseCatacombsRngMenu(event)
            "Heart of the Mountain" -> parseHotmMenu(event)
            "Frozen Corpse Milestones" -> parseCorpseMilestoneMenu(event)
            "Experimentation Table" -> parseExperimentationTableMenu(event)
        }
    }

    private fun parseRngMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 11, RngMeter.EXPERIMENTATION)
        readAndUpdateMeter(menu, 14, RngMeter.NUCLEUS)
        readAndUpdateMeter(menu, 15, RngMeter.FROZEN_CORPSE)
    }

    private fun parseSlayerRngMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 19, RngMeter.ZOMBIE)
        readAndUpdateMeter(menu, 20, RngMeter.SPIDER)
        readAndUpdateMeter(menu, 21, RngMeter.WOLF)
        readAndUpdateMeter(menu, 23, RngMeter.ENDERMAN)
        readAndUpdateMeter(menu, 24, RngMeter.VAMPIRE)
        readAndUpdateMeter(menu, 25, RngMeter.BLAZE)
    }

    private fun parseCatacombsRngMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 32, RngMeter.M5)
        readAndUpdateMeter(menu, 34, RngMeter.M7)
    }

    private fun parseHotmMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 51, RngMeter.NUCLEUS)
    }

    private fun parseCorpseMilestoneMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 50, RngMeter.FROZEN_CORPSE)
    }

    private fun parseExperimentationTableMenu(event: InventoryOpenEvent) {
        val menu = event.menu

        readAndUpdateMeter(menu, 48, RngMeter.EXPERIMENTATION)
    }

    private fun readAndUpdateMeter(menu: AbstractContainerMenu, slotIndex: Int, meterType : RngMeter) {
        val rngItem = menu.slots[slotIndex].item

        val (meter, selected) = readMeterItem(rngItem)
        //DyeAddons.debug("Read ${rngItem.hoverName.string} item as $meter meter and selected to be $selected")

        updateRngMeter(meterType, meter, selected)
    }

    private fun readMeterItem(slot : ItemStack): Pair<Int?, Boolean> {
        val meter = slot.findMatchInLore(METER_PATTERN)?.groupValues?.get(1)
            ?.replace(",","")
            ?.toIntOrNull()

        val selected = slot.findMatchInLore(SELECTED_PATTERN) != null

        return meter to selected
    }

    private fun updateRngMeter(meter : RngMeter, value : Int?, selected : Boolean) {
        if (value == null) return
        val meters = ProfileStorage.lastPlayedProfile()?.rngMeters ?: return

        when (meter) {
            RngMeter.ZOMBIE -> meters["zombie"] = MeterData(value, selected)
            RngMeter.SPIDER -> meters["spider"] = MeterData(value, selected)
            RngMeter.WOLF -> meters["wolf"] = MeterData(value, selected)
            RngMeter.ENDERMAN -> meters["enderman"] = MeterData(value, selected)
            RngMeter.BLAZE -> meters["blaze"] = MeterData(value, selected)
            RngMeter.VAMPIRE -> meters["vampire"] = MeterData(value, selected)
            RngMeter.EXPERIMENTATION -> meters["experimentation"] = MeterData(value, selected)
            RngMeter.NUCLEUS -> meters["nucleus"] = MeterData(value, selected)
            RngMeter.FROZEN_CORPSE -> meters["frozenCorpse"] = MeterData(value, selected)
            RngMeter.M5 -> meters["m5"] = MeterData(value, selected)
            RngMeter.M7 -> meters["m7"] = MeterData(value, selected)
        }
        DyeAddons.debug("Updated $meter meter to $value, dye selected is $selected", DebugCategories.MENU_EVENT)
    }

}