package anlg.dyeaddons.gui.widgets

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.gui.GuideScreen
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.min

class DyePanel(
    val dye : Dye,
    val dyesDropped : Int = 0,
    val dyeProgress : Double = 0.0,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    message: Component
) : AbstractWidget(x, y, width, height, message) {

    val dyeTexture = dye.getTexture()
    val progressBar = min(dyeProgress, 1.0)

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        val textRenderer = mc.font

        val iconSize = height / 3
        val padding = 3

        val inOverlay = ConfigManager.data.config.overlays["Dye:${dye}"]?.toggled ?: false

        // Draw Background
        context.fill(
            x + padding,
            y + padding,
            x + width - padding,
            y + height - padding,
            if (isHovered()) {
                Color(166, 166, 166, 100).rgb
            } else {
                Color(133, 133, 133, 100).rgb
            }
        )

        // Draw Dye Text
        context.text(
            textRenderer,
            dye.toString(),
            x + iconSize + 10,
            y + padding * 2 + textRenderer.lineHeight,
            Color(dye.color, false).rgb)

        // Draw Dye Description
        context.textWithWordWrap(
            textRenderer,
            net.minecraft.network.chat.FormattedText.of(dye.description),
            x + padding * 2,
            y + height / 2 + 12,
            width - 2 * padding - 10,
            Color(66, 66, 66, 255).rgb
        )

        // Draw Dye Texture
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            dyeTexture,
            x + padding * 2,
            y + padding * 2,
            0f, 0f,
            iconSize, iconSize,
            iconSize,
            iconSize)

        // Draw Dyes Dropped
        context.withScale(
            x + width - padding * 2,
            y + padding * 2,
            1.5f
        ) {
            context.text(
                textRenderer,
                dyesDropped.toString(),
                -textRenderer.width(dyesDropped.toString()),
                0,
                Color(dye.color, false).rgb
            )
        }

        // Draw Dye Progress bar
        context.fill(
            x + padding * 2,
            y + height / 2,
            x + width - padding * 2,
            y + height / 2 + 10,
            Color(111, 111, 111, 255).rgb
        )
        context.fill(
            x + padding * 2 + 1,
            y + height / 2 + 1,
            x + width - padding * 2 - 1,
            y + height / 2 + 9,
            Color(55, 55, 55, 255).rgb
        )
        context.fill(
            x + padding * 2 + 1,
            y + height / 2 + 1,
            (x + padding * 2 + 1) + (progressBar * (width - padding * 4 - 2)).toInt(),
            y + height / 2 + 9,
            Color(dye.color, false).rgb
        )
        val progressText = DecimalFormat("#.##%").format(dyeProgress)
        context.withScale(
            x + width - padding * 2,
            y + height / 2,
            1f
        ) {
            context.text(
                textRenderer,
                progressText,
                -textRenderer.width(progressText),
                -textRenderer.lineHeight,
                Color(dye.color, false).rgb
            )
        }

        // Highlight if toggled
        if (inOverlay) {
            val outlineWidth = 2f
            context.withScale(x, y, outlineWidth) {
                context.outline(
                    1,
                    1,
                    (width / outlineWidth).toInt() - 2,
                    (height / outlineWidth).toInt() - 1,
                    Color(dye.color, false).rgb
                )
            }
        }

        // Highlight red if stats not yet generated
        val incompleteStats = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.statistics?.isEmpty() ?: true
        if (incompleteStats && dye.statistics != null) {
            context.fill(
                x + padding,
                y + padding,
                x + width - padding,
                y + height - padding,
                Color(200, 50, 50, 50).rgb
            )
            setTooltip(
                Tooltip.create(Component.literal("Incomplete Statistics\n" +
                        "Left Click and navigate to statistics tab to complete!"))
            )
        }
    }

    override fun onClick(event: MouseButtonEvent, doubleClick: Boolean){
        super.onClick(event, doubleClick)
        when (event.buttonInfo.button) {
            0 -> mc.setScreen(GuideScreen(dye))
            1 -> ConfigManager.data.config.toggleOverlay("Dye:$dye")
        }
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

}