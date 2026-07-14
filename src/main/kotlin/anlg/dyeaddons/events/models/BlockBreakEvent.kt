package anlg.dyeaddons.events.models

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

data class BlockBreakEvent(
    val pos: BlockPos,
    val state: BlockState,
    val heldItem: ItemStack
)