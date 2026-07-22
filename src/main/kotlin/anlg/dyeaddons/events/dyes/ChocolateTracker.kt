package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import anlg.dyeaddons.utils.SkyblockUtils

object ChocolateTracker {

    private val CHOCOLATE_PATTERN = Regex("""(\d[\d,]*) Chocolate""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        val title = event.inventoryName

        if (!title.contains("Chocolate Factory")) return

        event.slots.filter {
                it.item.hoverName.string.contains("Chocolate")
        }.forEach { slot ->

            val hoverName = slot.item.hoverName

            val match = CHOCOLATE_PATTERN.find(hoverName.getFormattedString().removeFormatting())?.groupValues?.get(1)
            val chocolate = match?.replace(",","")?.toLongOrNull() ?: return@forEach

            updateDyeStats(chocolate)
            updateDyeProgress(chocolate)
            DyeAddons.debug("Tracked $chocolate chocolate", DebugCategories.MENU_EVENT)
        }

    }

    private fun updateDyeStats(chocolate : Long) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CHOCOLATE]?.statistics ?: return
        stats["Chocolate"] = CalcValue.LongVal(chocolate)
    }

    private fun updateDyeProgress(chocolate : Long) {
        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.CHOCOLATE]?.progress = chocolate / 40_000_000_000.0
    }


}