package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import java.awt.Color

class RotationOverlay(
    x : Int,
    y : Int,
    scale : Float,
    toggled : Boolean = true,
) : AbstractOverlay(
    x,
    y,
    120,
    50,
    scale,
    toggled,
) {
    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock() && super.shouldRender()
    }

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation?.multipliers ?: return
        val dyeTextures = dyeRotation.keys.associateWith { it.getTexture() }

        context.withScale(x, y, scale) {
            val textRenderer = mc.font

            // Draw Background
            context.fill(
                0,
                0,
                width,
                height,
                Color(50, 50, 50, 150).rgb
            )

            // Draw Dyes
            var currentX = 5

            dyeRotation.forEach { dye, multiplier ->
                // Image
                context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    dyeTextures[dye]!!,
                    currentX,
                    5,
                    0f, 0f,
                    30,
                    30,
                    30,
                    30
                )

                // Multiplier Text
                context.centeredText(
                    textRenderer,
                    "${multiplier}x",
                    currentX + 15,
                    40,
                    Color(255, 255, 255, 255).rgb
                )

                currentX += 40
            }
        }
    }
}