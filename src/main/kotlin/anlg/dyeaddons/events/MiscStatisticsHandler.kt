package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.config.VisitorData
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.InventoryUtils
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.network.chat.Component

// Grabs statistics for things not in api (visitors, bingo points, commissions, etc.)
object MiscStatisticsHandler {

    private val COMMISSION_PATTERN = Regex("""(\d[\d,]*)/[\d,]+""")

    private val RUNIC_KILLS_PATTERN = Regex("""Counter: (\d[\d,]*)""")


    private val BINGO_POINTS_PATTERN = Regex("""Bingo Points:.*?(\d[\d,]*)""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return
        val title = event.inventoryName

        when {
            title.contains("Commission Milestones") -> getCommissions(event)
            title.contains("Accessory Bag") -> getRunicKills(event)
            title.contains("Visitor's Logbook") -> getVisitors(event)
            title.contains("Bingo - ") -> getBingoPoints(event)
        }
    }

    private fun getCommissions(event : InventoryOpenEvent) {

        event.slots.filter {
            it.item.hoverName.contains(Component.literal("Milestone I Rewards"))
        }.forEach { slot ->

            val commissions = slot.item.findMatchInLore(COMMISSION_PATTERN)?.groupValues?.get(1)
                ?.replace(",","")
                ?.toIntOrNull() ?: return@forEach

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.statistics["Mining Commissions Completed"] = CalcValue.IntVal(commissions)
            ChatUtils.addLocalChatMessage("Grabbed commissions", true)
        }
    }

    private fun getRunicKills(event : InventoryOpenEvent) {
        event.slots.filter {
            it.item.hoverName.contains(Component.literal("Runebook"))
        }.forEach { slot ->

            val runicKills = slot.item.findMatchInLore(RUNIC_KILLS_PATTERN)?.groupValues?.get(1)
                ?.replace(",","")
                ?.toIntOrNull() ?: return@forEach

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PERIWINKLE]?.statistics["Runic Kills"] = CalcValue.IntVal(runicKills)
            DyeAddons.debug("Grabbed Runebook counter: $runicKills")
        }
    }

    private fun getVisitors(event : InventoryOpenEvent) {
        val notVisitors = listOf(" ", "Logbook", "Unlocked", "Sort", "Close", "Rarity", "Visited", "Offers Accepted", "Next Page", "Previous Page")

        val visitorList = mutableListOf<VisitorData>()

        event.slots.filter {
            it.item.hoverName.string !in notVisitors
        }.forEach { slot ->
            val visitor = InventoryUtils.parseVisitorItem(slot.item) ?: return@forEach
            visitorList.add(visitor)
        }

        val currentVisitorList = ProfileStorage.lastPlayedProfile()?.visitorData
        val visitorMap = visitorList.associateBy { it.name }

        val updatedList =
            currentVisitorList?.map { current -> visitorMap[current.name] ?: current }
                ?.plus(visitorList.filter { new -> currentVisitorList.none { it.name == new.name } })

        updatedList?.let { ProfileStorage.lastPlayedProfile()?.visitorData = updatedList }
        DyeAddons.debug("Grabbed data for ${visitorList.size} visitors")
    }

    private fun getBingoPoints(event : InventoryOpenEvent) {
        event.slots.filter {
                it.item.hoverName.string == "Bingo Shop"
        }.forEach { slot ->

            val bingoPoints = slot.item.findMatchInLore(BINGO_POINTS_PATTERN)?.groupValues?.get(1)
                ?.replace(",","")
                ?.toIntOrNull() ?: return@forEach

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BINGO_BLUE]?.statistics["Bingo Points"] = CalcValue.IntVal(bingoPoints)
            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BINGO_BLUE]?.progress = bingoPoints / 500.0
            DyeAddons.debug("Grabbed Bingo Points: $bingoPoints")
        }
    }
}