package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
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
    60,
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
        val textRenderer = mc.font

        val rotationYear = ConfigManager.data.config.currentDyeRotation?.year

        if (rotationYear == null || rotationYear != SkyblockUtils.skyblockTime.year) {
            context.withScale(x, y, scale) {
                context.fill(
                    0,
                    0,
                    width,
                    height,
                    Color(50, 50, 50, 150).rgb
                )

                context.textWithWordWrap(
                    textRenderer,
                    Component.literal("Dye rotation out of date! Talk to Vincent to load dye rotation"),
                    5,
                    5,
                    width - 10,
                    Color(255, 55, 55, 255).rgb
                )
            }
            return
        }

        val dyeRotation = ConfigManager.data.config.currentDyeRotation?.multipliers ?: return
        val dyeTextures = dyeRotation.keys.associateWith { it.getTexture() }

        context.withScale(x, y, scale) {
            // Draw Background
            context.fill(
                0,
                0,
                width,
                height,
                Color(50, 50, 50, 150).rgb
            )

            // Draw Title
            context.centeredText(
                textRenderer,
                "Year $rotationYear dyes",
                width / 2,
                5,
                Color(255, 255, 255, 255).rgb
            )

            // Draw Dyes
            var currentX = 5

            dyeRotation.forEach { dye, multiplier ->
                // Image
                context.blit(
                    RenderPipelines.GUI_TEXTURED,
                    dyeTextures[dye]!!,
                    currentX,
                    15,
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
                    50,
                    Color(255, 255, 255, 255).rgb
                )

                currentX += 40
            }
        }
    }
}