package anlg.dyeaddons.events.models

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

data class OreMinedEvent(
    val blocks: List<MinedBlock>
)

data class MinedBlock(
    val pos: BlockPos,
    val state: BlockState
)