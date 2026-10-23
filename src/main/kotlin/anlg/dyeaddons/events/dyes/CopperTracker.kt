package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.config.VisitorData
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

object CopperTracker {

    private val dye = Dye.COPPER

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
            updateDyeStats(visitor)
            updateDyeProgress(visitor, charmed)
        } else if (visitor.visits > storedVisitor.visits) {
            storedVisitor.visits = visitor.visits
            updateDyeStats(visitor)
            updateDyeProgress(visitor, charmed)
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

        val dropRate = 1.0 / visitor.rarity.baseChance *
                (if (charmed) dye.getDropRate("Charm") else 1.0) *
                stats.getDyeMultiplier(dye)

        FakeDyeDrop.rollFakeDyeDrop(
            dye,
            visitor.rarity.baseChance,
            dropRate,
        )

        if (visitor.name == "Vincent") {
            val dropRate = 1.0 / Dye.WILD_STRAWBERRY.getDropRate("Vincent") *
                    (if (charmed) Dye.WILD_STRAWBERRY.getDropRate("Charm") else 1.0) *
                    stats.getDyeMultiplier(
                        Dye.WILD_STRAWBERRY,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.BUCKET_OF_DYE,
                        DyeMultiplier.MIRACLE_CHANCE,
                        DyeMultiplier.PAINT_PALETTE,
                        DyeMultiplier.FOUR_LEAF_CLOVER)

            FakeDyeDrop.rollFakeDyeDrop(
                Dye.WILD_STRAWBERRY,
                Dye.WILD_STRAWBERRY.getDropRate("Vincent"),
                dropRate,
            )
        }
    }

    private fun updateDyeStats(visitor : VisitorData) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (visitor.rarity) {
            Visitor.UNCOMMON -> stats.incrementStat(dye, "Uncommon Visitor Visits")
            Visitor.RARE -> stats.incrementStat(dye, "Rare Visitor Visits")
            Visitor.LEGENDARY -> stats.incrementStat(dye, "Legendary Visitor Visits")
            Visitor.MYTHIC -> stats.incrementStat(dye, "Mythic Visitor Visits")
            Visitor.SPECIAL -> stats.incrementStat(dye, "Special Visitor Visits")
        }
        if (visitor.name == "Vincent") {
            stats.incrementStat(Dye.WILD_STRAWBERRY, "Vincent Visitor Visits")
        }
    }

    private fun updateDyeProgress(visitor : VisitorData, charmed : Boolean = false) {
        DyeAddons.debug("Tracked ${visitor.name} visitor visit, charmed = $charmed", DebugCategories.DYE_PROGRESS_EVENT)
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / visitor.rarity.baseChance *
                (if (charmed) dye.getDropRate("Charm") else 1.0) *
                stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        if (visitor.name == "Vincent") {
            val dropRate = 1.0 / Dye.WILD_STRAWBERRY.getDropRate("Vincent") *
                    (if (charmed) Dye.WILD_STRAWBERRY.getDropRate("Charm") else 1.0) *
                    stats.getDyeMultiplier(
                        Dye.WILD_STRAWBERRY,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.BUCKET_OF_DYE,
                        DyeMultiplier.MIRACLE_CHANCE,
                        DyeMultiplier.PAINT_PALETTE,
                        DyeMultiplier.FOUR_LEAF_CLOVER)

            ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.progress += dropRate
        }
    }
}