package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.FormattedText
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.min

class DyePanelOverlay(
    x : Int,
    y : Int,
    scale : Float,
    toggled : Boolean = false,
    val dye : Dye,
) : AbstractOverlay(
    x,
    y,
    150,
    50,
    scale,
    toggled,
) {

    val dyeTexture = dye.getTexture()

    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock() && super.shouldRender()
    }

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        val textRenderer = mc.font

        val dyeProgress = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress ?: 0.0
        val dyesDropped = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped ?: 0

        val progressBar = min(dyeProgress, 1.0)

        context.withScale(x, y, scale) {
            // Draw Background
            context.fill(
                0,
                0,
                width,
                height,
                Color(100, 100, 100, 200).rgb
            )
            // Draw Dye Text
            context.text(
                textRenderer,
                dye.toString(),
                35,
                10,
                Color(dye.color, false).rgb)

            // Draw Dye Description
            context.withScale(2, height - 12, 0.65f) {
                context.textWithWordWrap(
                    textRenderer,
                    FormattedText.of(dye.description),
                    0,
                    0,
                    (width / 0.65f).toInt() - 5,
                    Color(66, 66, 66, 255).rgb,
                )
            }

            // Draw Dye Texture
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                dyeTexture,
                3,
                3,
                0f, 0f,
                25,
                25,
                25,
                25)

            // Draw Dyes Dropped
            context.text(
                textRenderer,
                dyesDropped.toString(),
                width - 2 - textRenderer.width(dyesDropped.toString()),
                2,
                Color(dye.color, false).rgb
            )

            // Draw Dye Progress bar
            context.fill(
                2,
                height - 18,
                width - 2,
                height - 12,
                Color(111, 111, 111, 255).rgb
            )
            context.fill(
                3,
                height - 17,
                width - 3,
                height - 13,
                Color(55, 55, 55, 255).rgb
            )
            context.fill(
                3,
                height - 17,
                3 + (progressBar * (width - 6)).toInt(),
                height - 13,
                Color(dye.color, false).rgb
            )
            val progressText = DecimalFormat("#.##%").format(dyeProgress)
            context.withScale(
                width - 3,
                height - 17 - textRenderer.lineHeight,
                0.75f) {
                context.text(
                    textRenderer,
                    progressText,
                    -textRenderer.width(progressText),
                    0,
                    Color(dye.color, false).rgb
                )
            }
        }
    }
}