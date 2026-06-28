package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.logger
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.config.VisitorData
import anlg.dyeaddons.config.VisitorRarity
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

// Grabs statistics for things not in api (visitors, bingo points, commissions, etc.)
object MiscStatisticsHandler {

    private val COMMISSION_PATTERN = Regex("""(\d[\d,]*)/[\d,]+""")

    private val RUNIC_KILLS_PATTERN = Regex("""Counter: (\d[\d,]*)""")

    private val VISITS_PATTERN = Regex("""Times Visited:.*?(\d[\d,]*)""")

    private val BINGO_POINTS_PATTERN = Regex("""Bingo Points:.*?(\d[\d,]*)""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        val menu = event.screen.menu
        val title = event.screen.getTitle().string

        if (title.contains("§eCommission Milestones") && SkyblockUtils.hypixelMain) getCommissions(menu)
        if (title.contains("Accessory Bag") && SkyblockUtils.hypixelMain) getRunicKills(menu)
        if (title.contains("Visitor's Logbook") && SkyblockUtils.hypixelMain) getVisitors(menu)
        if (title.contains("Bingo - ") && SkyblockUtils.hypixelMain) getBingoPoints(menu)
    }

    private fun getCommissions(menu : AbstractContainerMenu) {

        menu.slots.filter { !it.item.isEmpty &&
                it.item.hoverName.contains(Component.literal("Milestone I Rewards")) &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString(" ") { it.string } ?: ""

            val match = COMMISSION_PATTERN.find(loreText)?.groupValues?.get(1)
            val commissions = match?.replace(",","")?.toIntOrNull() ?: 0

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.NYANZA]?.statistics["Mining Commissions Completed"] = CalcValue.IntVal(commissions)
            ChatUtils.addLocalChatMessage("Grabbed commissions", true)
        }
    }

    private fun getRunicKills(menu : AbstractContainerMenu) {
        menu.slots.filter { !it.item.isEmpty &&
                it.item.hoverName.contains(Component.literal("Runebook")) &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString(" ") { it.string } ?: ""

            val match = RUNIC_KILLS_PATTERN.find(loreText)?.groupValues?.get(1)
            val runicKills = match?.replace(",","")?.toIntOrNull() ?: 0

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PERIWINKLE]?.statistics["Runic Kills"] = CalcValue.IntVal(runicKills)
            ChatUtils.addLocalChatMessage("Grabbed Runebook counter", true)
        }
    }

    private fun getVisitors(menu : AbstractContainerMenu) {
        val notVisitors = listOf(" ", "Logbook", "Unlocked", "Sort", "Close", "Rarity", "Visited", "Offers Accepted", "Next Page", "Previous Page")

        val visitorList = mutableListOf<VisitorData>()

        menu.slots.filter { !it.item.isEmpty &&
                it.item.hoverName.string !in notVisitors &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString("|") { it.string } ?: ""

            val name = slotItemStack.hoverName.string
            val rarity = loreText.trim().split("|").getOrNull(0) ?: ""

            val match = VISITS_PATTERN.find(loreText)?.groupValues?.get(1)
            val visits = match?.replace(",","")?.toIntOrNull() ?: 0

            try {
                val visitor = VisitorData(name, VisitorRarity.valueOf(rarity), visits)
                visitorList.add(visitor)
            } catch (e: IllegalArgumentException) {
                logger.info("Could not parse visitor: $name")
            }
        }

        val currentVisitorList = ProfileStorage.lastPlayedProfile()?.visitorData
        val visitorMap = visitorList.associateBy { it.name }

        val updatedList =
            currentVisitorList?.map { current -> visitorMap[current.name] ?: current }
                ?.plus(visitorList.filter { new -> currentVisitorList.none { it.name == new.name } })

        updatedList?.let { ProfileStorage.lastPlayedProfile()?.visitorData = updatedList }
        ChatUtils.addLocalChatMessage("Grabbed data for ${visitorList.size} visitors", true)
    }

    private fun getBingoPoints(menu : AbstractContainerMenu) {
        menu.slots.filter { !it.item.isEmpty &&
                it.item.hoverName.string == "Bingo Shop" &&
                it.container !is Inventory
        }.forEach { slot ->

            val slotItemStack = slot.item

            val lore = slotItemStack.get(DataComponents.LORE)
            val loreText = lore?.lines()?.joinToString("|") { it.string } ?: ""

            val match = BINGO_POINTS_PATTERN.find(loreText)?.groupValues?.get(1)
            val bingoPoints = match?.replace(",","")?.toIntOrNull() ?: 0

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BINGO_BLUE]?.statistics["Bingo Points"] = CalcValue.IntVal(bingoPoints)
            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.BINGO_BLUE]?.progress = bingoPoints / 500.0
            ChatUtils.addLocalChatMessage("Grabbed Bingo Points", true)
        }
    }
}