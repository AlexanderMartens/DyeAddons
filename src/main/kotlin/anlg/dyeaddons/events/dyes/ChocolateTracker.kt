package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.CalcValue
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.world.entity.player.Inventory

object ChocolateTracker {

    private val CHOCOLATE_PATTERN = Regex("""(\d[\d,]*) Chocolate""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(@Suppress("UNUSED_PARAMETER") event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        val menu = event.screen.menu
        val title = event.screen.getTitle().string

        if (!title.contains("Chocolate Factory") || !SkyblockUtils.hypixelMain) return

        menu.slots.filter { !it.item.isEmpty &&
                it.item.hoverName.string.contains("Chocolate") &&
                it.container !is Inventory
        }.forEach { slot ->

            val hoverName = slot.item.hoverName

            val match = CHOCOLATE_PATTERN.find(hoverName.getFormattedString().removeFormatting())?.groupValues?.get(1)
            val chocolate = match?.replace(",","")?.toLongOrNull() ?: return@forEach

            updateDyeStats(chocolate)
            updateDyeProgress(chocolate)
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