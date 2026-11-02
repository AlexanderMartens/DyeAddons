package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.SubmitEntityNameTag;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<S extends EntityRenderState> {

    @Inject(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void dyeaddons$onSubmitNameDisplay(
            S state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            int offset,
            CallbackInfo ci
    ) {
        EventBus.INSTANCE.publish(
                new SubmitEntityNameTag(state, poseStack, submitNodeCollector, camera, offset, ci)
        );
    }
}
