package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.events.models.SlotClickEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.InventoryUtils
import anlg.dyeaddons.utils.InventoryUtils.findMatchInLore
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.Visitor
import anlg.dyeaddons.utils.extensions.incrementInt

object CopperTracker {

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
        EventBus.subscribe(SlotClickEvent::class, ::onSlotClick)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        val menu = event.screen.menu
        val title = event.inventoryName
        val visitorItem = menu.slots[13].item
        val rewardsItem = menu.slots[29].item

        if (title != visitorItem.hoverName.string) return

        val visitor = InventoryUtils.parseVisitorItem(visitorItem) ?: return
        val charmed = rewardsItem.findMatchInLore(Regex("""Visitors' Gratitude""")) != null
        val storedVisitor = ProfileStorage.lastPlayedProfile()?.visitorData?.firstOrNull { it.name == visitor.name }

        if (storedVisitor == null) {
            val oldVisitorData = ProfileStorage.lastPlayedProfile()?.visitorData
            val newVisitorData = oldVisitorData?.plus(visitor)
            if (newVisitorData != null) {
                ProfileStorage.lastPlayedProfile()?.visitorData = newVisitorData
            }
            updateDyeStats(visitor.rarity)
            updateDyeProgress(visitor.rarity, charmed)
        } else if (visitor.visits > storedVisitor.visits) {
            storedVisitor.visits = visitor.visits
            updateDyeStats(visitor.rarity)
            updateDyeProgress(visitor.rarity, charmed)
        }
    }

    private fun onSlotClick(event: SlotClickEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        val menu = event.container.menu
        val title = event.container.title.string
        val visitorItem = menu.slots[13].item
        val rewardsItem = menu.slots[29].item

        if (event.slotId != 29 ||
            rewardsItem.findMatchInLore(Regex("""Missing items to accept!""")) != null) return

        if (title != visitorItem.hoverName.string) return

        val visitor = InventoryUtils.parseVisitorItem(visitorItem) ?: return
        val charmed = rewardsItem.findMatchInLore(Regex("""Visitors' Gratitude""")) != null

        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / visitor.rarity.baseChance * (if (charmed) 3.0 else 1.0) * stats.getDyeMultiplier(
            Dye.COPPER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        FakeDyeDrop.rollFakeDyeDrop(
            Dye.COPPER,
            visitor.rarity.baseChance.toDouble(),
            dropRate,
        )

        if (visitor.name == "Vincent") {
            val dropRate = 1.0 / 2_500.0 * (if (charmed) 3.0 else 1.0) * stats.getDyeMultiplier(
                Dye.COPPER,
                DyeMultiplier.VINCENT,
                DyeMultiplier.BUCKET_OF_DYE,
                DyeMultiplier.MIRACLE_CHANCE)

            FakeDyeDrop.rollFakeDyeDrop(
                Dye.WILD_STRAWBERRY,
                2_500.0,
                dropRate,
            )
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
        if (visitor.name == "Vincent") {
            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.statistics?.incrementInt("Vincent Visitor Visits")
        }
    }

    private fun updateDyeProgress(visitor : Visitor, charmed : Boolean = false) {
        DyeAddons.debug("Tracked ${visitor.name} visitor visit, charmed = $charmed", DebugCategories.DYE_PROGRESS_EVENT)
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / visitor.baseChance * (if (charmed) 3.0 else 1.0) * stats.getDyeMultiplier(
            Dye.COPPER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.COPPER]?.progress += dropRate

        if (visitor.name == "Vincent") {
            val dropRate = 1.0 / 2_500.0 * (if (charmed) 3.0 else 1.0) * stats.getDyeMultiplier(
                Dye.COPPER,
                DyeMultiplier.VINCENT,
                DyeMultiplier.BUCKET_OF_DYE,
                DyeMultiplier.MIRACLE_CHANCE)

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.progress += dropRate
        }
    }
}