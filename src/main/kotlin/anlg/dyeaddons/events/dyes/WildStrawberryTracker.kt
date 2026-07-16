package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.BlockBreakEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.utils.InventoryUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.IntegerProperty

object WildStrawberryTracker {

    private var lastLocation: BlockPos? = null

    fun init() {
        EventBus.subscribe(InventoryOpenEvent::class, ::onInventoryOpen)
        EventBus.subscribe(BlockBreakEvent::class, ::onBlockBreak)
    }

    private fun onInventoryOpen(event: InventoryOpenEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        val menu = event.screen.menu
        val title = event.inventoryName
        val visitorItem = menu.slots[13].item

        if (title != "Vincent") return

        val visitor = InventoryUtils.parseVisitorItem(visitorItem) ?: return
        val storedVisitor = ProfileStorage.lastPlayedProfile()?.visitorData?.firstOrNull { it.name == "Vincent" }

        if (storedVisitor == null) {
            val oldVisitorData = ProfileStorage.lastPlayedProfile()?.visitorData
            val newVisitorData = oldVisitorData?.plus(visitor)
            if (newVisitorData != null) {
                ProfileStorage.lastPlayedProfile()?.visitorData = newVisitorData
            }
            updateDyeStats(false)
            updateDyeProgress(false)
        } else if (visitor.visits > storedVisitor.visits) {
            storedVisitor.visits = visitor.visits
            updateDyeStats(false)
            updateDyeProgress(false)
        }

    }

    private fun onBlockBreak(event: BlockBreakEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Garden") return

        val cropBlocks = listOf(
            Blocks.WHEAT,
            Blocks.CARROTS,
            Blocks.POTATOES,
            Blocks.CARVED_PUMPKIN,
            Blocks.SUGAR_CANE,
            Blocks.MELON,
            Blocks.CACTUS,
            Blocks.COCOA,
            Blocks.RED_MUSHROOM,
            Blocks.BROWN_MUSHROOM,
            Blocks.NETHER_WART,
            Blocks.ROSE_BUSH,
            Blocks.SUNFLOWER
        )
        if (event.state.block !in cropBlocks) return
        if (event.state.block !in listOf(Blocks.CACTUS, Blocks.SUGAR_CANE) && event.state.isBabyCrop()) return

        if (lastLocation == event.pos) return

        lastLocation = event.pos
        updateDyeStats(true)
        updateDyeProgress(true)
    }

    private fun updateDyeStats(crop : Boolean) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.statistics ?: return

        if (crop) {
            stats.incrementInt("Crop Blocks Broken")
        } else {
            stats.incrementInt("Vincent Visitor Visits")
        }

    }

    private fun updateDyeProgress(crop : Boolean) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.WILD_STRAWBERRY) ?: 1

        val overbloom = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.statistics["Overbloom"]?.asFloat() ?: 0f

        val chance = if (crop) (1.0 / 150_000_000.0) * (1.0 + overbloom / 100.0) else (1.0 / 2_500.0)
        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.WILD_STRAWBERRY]?.progress += chance * multiplier
    }

    fun BlockState.isBabyCrop(): Boolean {
        val property = (block.stateDefinition.properties.find { it.name == "age" } as? IntegerProperty) ?: return false
        return getValue(property) == 0
    }

}