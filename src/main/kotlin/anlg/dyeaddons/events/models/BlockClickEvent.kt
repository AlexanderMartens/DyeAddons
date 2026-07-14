package anlg.dyeaddons.events.models

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

enum class InteractClickType {
    LEFT_CLICK,
    RIGHT_CLICK
}

data class BlockClickEvent(
    val clickType: InteractClickType,
    val pos: BlockPos,
    val state: BlockState?,
    val itemInHand: ItemStack?
)