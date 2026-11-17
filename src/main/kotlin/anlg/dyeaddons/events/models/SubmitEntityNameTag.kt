package anlg.dyeaddons.events.models

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

data class SubmitEntityNameTag(
    val state: EntityRenderState,
    val poseStack: PoseStack,
    val nodeCollector: SubmitNodeCollector,
    val camera: CameraRenderState,
    val offset: Int,
    val callbackInfo: CallbackInfo
)