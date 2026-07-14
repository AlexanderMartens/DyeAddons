package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.config.VisitorData
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.Visitor
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.core.component.DataComponents

object CopperTracker {

    private val VISITS_PATTERN = Regex("""Times Visited:.*?(\d[\d,]*)""")

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
    }

    private fun onInventoryOpen(@Suppress("UNUSED_PARAMETER") event: InventoryOpenEvent) {
        if (SkyblockUtils.getWorldName() != "Garden" || !SkyblockUtils.hypixelMain) return

        val menu = event.screen.menu
        val title = event.screen.getTitle().string
        val visitorItem = menu.slots[13].item

        if (title != visitorItem.hoverName.string) return

        val lore = visitorItem.get(DataComponents.LORE)
        val loreText = lore?.lines()?.joinToString("|") { it.string } ?: ""

        val visitorName = visitorItem.hoverName.string
        val visitorRarity = loreText.trim().split("|").getOrNull(0) ?: ""

        val match = VISITS_PATTERN.find(loreText)?.groupValues?.get(1)
        val visits = match?.replace(",","")?.toIntOrNull() ?: 0

        try {
            val storedVisitor = ProfileStorage.lastPlayedProfile()?.visitorData?.firstOrNull { it.name == visitorName }
            val visitor = VisitorData(visitorName, Visitor.valueOf(visitorRarity), visits)
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
            }
        } catch (_: IllegalArgumentException) {
            DyeAddons.debug("Could not parse visitor: $visitorName")
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