package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.features.dye.DyeTracker
import anlg.dyeaddons.features.dye.TrackerState
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.currentScreen
import anlg.dyeaddons.utils.extensions.renderElement
import anlg.dyeaddons.utils.extensions.withScale
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderPipelines
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
    40,
    scale,
    toggled,
) {

    val dyeTexture = dye.getTexture()

    private val buttons = mutableListOf<OverlayButton>()

    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock() && super.shouldRender()
    }

    //? if >=26.1 {
    override fun extractRenderState(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderPanel(context, deltaTracker)
    //?} else {
    /*override fun render(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) = renderPanel(context, deltaTracker)
    *///?}

    private fun renderPanel(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        val textRenderer = mc.font

        val dyeProgress = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress ?: 0.0
        val dyesDropped = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped ?: 0

        val progressBar = min(dyeProgress, 1.0)

        buttons.clear()

        context.withScale(x, y, scale) {
            // Draw Background
            context.fill(
                0,
                0,
                width,
                height,
                Color(50, 50, 50, 150).rgb
            )
            // Draw Dye Text
            context.text(
                textRenderer,
                dye.toString(),
                30,
                3,
                Color(dye.color, false).rgb)

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

            // Draw Tracker ETA and tracker buttons
            val tracker = DyeTracker.trackers[dye]
            tracker?.let {
                val eta = tracker.getFormattedETA()
                if (tracker.getETA() > 0L && tracker.state != TrackerState.NOT_STARTED) {
                    context.withScale(30, 20, 0.75f) {
                        context.text(
                            textRenderer,
                            "ETA: $eta",
                            0,
                            0,
                            Color(dye.color, false).rgb
                        )
                    }
                }
                if (mc.currentScreen() is InventoryScreen || mc.currentScreen() is ChatScreen) {
                    when (tracker.state) {
                        TrackerState.NOT_STARTED -> {
                            buttons.add(OverlayButton(
                                width - 32,
                                11,
                                30,
                                10,
                                "Start",
                                Color(55, 200, 55, 255).rgb,
                                { tracker.start() },
                                0.65f
                            ))
                        }
                        TrackerState.RUNNING -> {
                            buttons.add(OverlayButton(
                                width - 32,
                                11,
                                30,
                                10,
                                "Stop",
                                Color(255, 55, 55, 255).rgb,
                                { tracker.stop() },
                                0.65f
                            ))                        }
                        TrackerState.STOPPED -> {
                            buttons.add(OverlayButton(
                                width - 32,
                                11,
                                30,
                                10,
                                "Resume",
                                Color(200, 200, 55, 255).rgb,
                                { tracker.resume() },
                                0.65f
                            ))
                            buttons.add(OverlayButton(
                                width - 64,
                                11,
                                30,
                                10,
                                "Reset",
                                Color(255, 55, 55, 255).rgb,
                                { tracker.reset() },
                                0.65f
                            ))
                        }
                    }
                }
            }

            buttons.forEach { button ->
                button.renderElement(context, deltaTracker)
            }

            // Draw Dye Progress bar
            context.withScale(0, height - 10, 1f) {
                context.fill(
                    2,
                    0,
                    width - 2,
                    8,
                    Color(111, 111, 111, 255).rgb
                )
                context.fill(
                    3,
                    1,
                    width - 3,
                    7,
                    Color(55, 55, 55, 255).rgb
                )
                context.fill(
                    3,
                    1,
                    3 + (progressBar * (width - 6)).toInt(),
                    7,
                    Color(dye.color, false).rgb
                )
                val progressText = DecimalFormat("#.##%").format(dyeProgress)
                context.withScale(
                    width - 3,
                    1 - textRenderer.lineHeight,
                    0.85f) {
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

    override fun onClick(mouseX : Double, mouseY : Double) {
        buttons.forEach { button ->
            button.onClick(mouseX, mouseY)
        }
    }
}