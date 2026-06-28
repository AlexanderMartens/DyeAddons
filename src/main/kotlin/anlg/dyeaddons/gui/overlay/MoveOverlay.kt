package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.data.ColorCodes.*
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color
import kotlin.math.max

class MoveOverlaysScreen : Screen(Component.literal("DyeAddons Move Overlays")) {

    private val enabledOverlays = Overlay.registeredElements.filter { it.shouldRender() }
    private var isDraggingOverlay: AbstractOverlay? = null
    private var dragOffsetX = 0
    private var dragOffsetY = 0
    private var lastDraggedOverlay: AbstractOverlay? = null

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {

        val textRenderer = mc.font

        // Draw background
        context.fill(
            0,
            0,
            width,
            height,
            Color(0, 0, 0, 125).rgb
        )

        // Draw hints
        context.centeredText(
            textRenderer,
            Component.literal("${YELLOW}Enable Overlays to see them here!"),
            width / 2,
            10,
            Color(255, 255, 255, 255).rgb
        )
        context.centeredText(
            textRenderer,
            Component.literal("${YELLOW}Move them with your mouse, scroll to scale. Press ESC to exit."),
            width / 2,
            20,
            Color(255, 255, 255, 255).rgb
        )

        // Draw samples
        enabledOverlays.forEach { overlay ->
            overlay.drawSample(context)
        }


        super.extractRenderState(context, mouseX, mouseY, a)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick)
        val mouseX = event.x()
        val mouseY = event.y()

        enabledOverlays.forEach { overlay ->
            val isInSample = overlay.isInSample(mouseX, mouseY)

            if (!isInSample) return@forEach

            isDraggingOverlay = overlay
            lastDraggedOverlay = overlay

            dragOffsetX = (mouseX - overlay.x).toInt()
            dragOffsetY = (mouseY - overlay.y).toInt()
            return true
        }

        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        if (event.button() != 0 || isDraggingOverlay == null) return super.mouseDragged(event, dx, dy)
        val overlay = isDraggingOverlay!!
        val mouseX = event.x()
        val mouseY = event.y()

        val newX = (mouseX - dragOffsetX).toInt().coerceIn(0, max(0, width - (overlay.width * overlay.scale).toInt()))
        val newY = (mouseY - dragOffsetY).toInt().coerceIn(0, max(0, height - (overlay.height * overlay.scale).toInt()))

        overlay.x = newX
        overlay.y = newY

        when (overlay) {
            is DyePanelOverlay -> {
                ConfigManager.data.config.dyeOverlays[overlay.dye] = OverlayConfig(
                    overlay.x,
                    overlay.y,
                    overlay.scale,
                    true
                )
            }

            is RotationOverlay -> {
                ConfigManager.data.config.rotationOverlay = OverlayConfig(
                    overlay.x,
                    overlay.y,
                    overlay.scale,
                    true
                )
            }
        }

        return super.mouseDragged(event, dx, dy)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (event.button() == 0) {
            isDraggingOverlay = null
        }
        return super.mouseReleased(event)
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
        if (lastDraggedOverlay != null && scrollY != 0.0) {
            val delta = if (scrollY > 0) 0.1f else -0.1f
            lastDraggedOverlay!!.scale = (lastDraggedOverlay!!.scale + delta).coerceAtLeast(0.2f)

            when (val overlay = lastDraggedOverlay!!) {
                is DyePanelOverlay -> {
                    ConfigManager.data.config.dyeOverlays[overlay.dye] = OverlayConfig(
                        overlay.x,
                        overlay.y,
                        overlay.scale,
                        true
                    )
                }

                is RotationOverlay -> {
                    ConfigManager.data.config.rotationOverlay = OverlayConfig(
                        overlay.x,
                        overlay.y,
                        overlay.scale,
                        true
                    )
                }
            }
            return true
        }
        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onClose() {
        ConfigManager.save()
        super.onClose()
    }
}