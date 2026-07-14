package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.OreMinedEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.world.level.block.Blocks

object EmeraldTracker {

    fun init() {
        EventBus.subscribe(OreMinedEvent::class, ::onOreMined)
    }

    private fun onOreMined(@Suppress("UNUSED_PARAMETER") event: OreMinedEvent) {
        if (!SkyblockUtils.hypixelMain || !SkyblockUtils.isInSkyblock()) return

        val emeraldBlocks = event.blocks.filter { it.state.block == Blocks.EMERALD_BLOCK || it.state.block == Blocks.EMERALD_ORE }

        if (emeraldBlocks.isEmpty()) return

        DyeAddons.debug("Broke ${emeraldBlocks.size} emerald blocks")
        val totalBlocks = emeraldBlocks.size
        updateDyeStats(totalBlocks)
        updateDyeProgress(totalBlocks)
    }

    private fun updateDyeStats(numBlocks: Int) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.EMERALD]?.statistics ?: return

        stats.incrementInt("Emerald Blocks Mined", numBlocks)
    }

    private fun updateDyeProgress(numBlocks: Int) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.EMERALD) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.EMERALD]?.progress += (1.0 / 5_000_000.0) * numBlocks * multiplier
    }

}