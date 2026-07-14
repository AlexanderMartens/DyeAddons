package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.BlockBreakEvent;
import anlg.dyeaddons.events.models.BlockClickEvent;
import anlg.dyeaddons.events.models.InteractClickType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void dyeaddons$onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        BlockState state = mc.level.getBlockState(pos);
        ItemStack held = mc.player.getMainHandItem();

        EventBus.INSTANCE.publish(new BlockBreakEvent(pos, state, held));
    }

    @Inject(method = "startDestroyBlock", at = @At("HEAD"))
    private void dyeaddons$leftClickBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        BlockState state = mc.level.getBlockState(pos);
        ItemStack held = mc.player.getMainHandItem();

        EventBus.INSTANCE.publish(new BlockClickEvent(InteractClickType.LEFT_CLICK, pos, state, held));
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void dyeaddons$rightClickBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return;

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        ItemStack held = mc.player.getItemInHand(hand);

        EventBus.INSTANCE.publish(new BlockClickEvent(InteractClickType.RIGHT_CLICK, pos, state, held));
    }

}