package anlg.dyeaddons.events.models

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

data class ServerBlockChangeEvent(
    val pos: BlockPos,
    val oldState: BlockState,
    val newState: BlockState
)