package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.Visitor
import anlg.dyeaddons.utils.extensions.incrementInt

object CopperTracker {

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        val menu = event.screen.menu
        val title = event.inventoryName
        val visitorItem = menu.slots[13].item

        if (title != visitorItem.hoverName.string) return

        val visitor = InventoryUtils.parseVisitorItem(visitorItem) ?: return
        val storedVisitor = ProfileStorage.lastPlayedProfile()?.visitorData?.firstOrNull { it.name == visitor.name }

        if (storedVisitor == null) {
            val oldVisitorData = ProfileStorage.lastPlayedProfile()?.visitorData
            val newVisitorData = oldVisitorData?.plus(visitor)
            if (newVisitorData != null) {
                ProfileStorage.lastPlayedProfile()?.visitorData = newVisitorData
            }
            updateDyeStats(visitor.rarity)
            updateDyeProgress(visitor.rarity)
        } else if (visitor.visits > storedVisitor.visits) {
            storedVisitor.visits = visitor.visits
            updateDyeStats(visitor.rarity)
            updateDyeProgress(visitor.rarity)
            DyeAddons.debug("Tracked ${visitor.name} visitor visit", DebugCategories.DYE_PROGRESS_EVENT)
        }

    }

    private fun updateDyeStats(visitor : Visitor) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.COPPER]?.statistics ?: return

        when (visitor) {
            Visitor.UNCOMMON -> stats.incrementInt("Uncommon Visitor Visits")
            Visitor.RARE -> stats.incrementInt("Rare Visitor Visits")
            Visitor.LEGENDARY -> stats.incrementInt("Legendary Visitor Visits")
            Visitor.MYTHIC -> stats.incrementInt("Mythic Visitor Visits")
            Visitor.SPECIAL -> stats.incrementInt("Special Visitor Visits")
        }
    }

    private fun updateDyeProgress(visitor : Visitor) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.COPPER) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.COPPER]?.progress += (1.0 / visitor.baseChance) * multiplier
    }

}