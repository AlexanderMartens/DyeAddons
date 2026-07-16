package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ActionBarEvent
import anlg.dyeaddons.events.models.BlockClickEvent
import anlg.dyeaddons.events.models.InteractClickType
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

object SecretTracker {

    private val chestsClicked : MutableList<BlockPos> = mutableListOf()

    private var inSecretRoom = false

    fun init() {
        EventBus.subscribe(BlockClickEvent::class, ::onBlockClick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
        EventBus.subscribe(ActionBarEvent::class, ::onActionBar)
    }

    private fun onBlockClick(event: BlockClickEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        if (!inSecretRoom) return

        if (event.clickType != InteractClickType.RIGHT_CLICK) return

        if (event.state == null || event.state.block !in listOf(Blocks.CHEST, Blocks.TRAPPED_CHEST)) return

        if (event.pos in chestsClicked) return

        chestsClicked.add(event.pos)

        updateDyeStats()
        updateDyeProgress()
        DyeAddons.debug("Tracked secret chest opened")
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        chestsClicked.clear()
    }

    private fun onActionBar(event: ActionBarEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            TabListUtils.getLineAfter("Dungeon:").trim() != "Catacombs") return

        inSecretRoom = event.unformattedText.trim().contains("Secrets")
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.SECRET]?.statistics ?: return

        stats.incrementInt("Catacombs Secrets Collected")
    }

    private fun updateDyeProgress() {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.SECRET) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.SECRET]?.progress += (1.0 / 1_000_000.0) * multiplier
    }

}