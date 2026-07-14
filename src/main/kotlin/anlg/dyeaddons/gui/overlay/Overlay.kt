package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.config.ConfigManager
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

object Overlay : HudElement {

    var registeredElements = mutableListOf<AbstractOverlay>()

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        registeredElements = ConfigManager.data.config.dyeOverlays.map {
            DyePanelOverlay(
                it.value.x,
                it.value.y,
                it.value.scale,
                it.value.toggled,
                it.key
            )
        }.toMutableList()

        val rotationOverlay = ConfigManager.data.config.rotationOverlay
        registeredElements.add(
            RotationOverlay(
                rotationOverlay.x,
                rotationOverlay.y,
                rotationOverlay.scale,
                rotationOverlay.toggled,
            )
        )

        registeredElements.forEach { element ->
            if (element.shouldRender()) element.extractRenderState(context, deltaTracker)
        }
    }

    fun resetOverlays() {
        ConfigManager.data.config.dyeOverlays.values.forEach { element ->
            element.x = 0
            element.y = 0
            element.scale = 1f
        }
        val rotationOverlay = ConfigManager.data.config.rotationOverlay
        rotationOverlay.x = 0
        rotationOverlay.y = 0
        rotationOverlay.scale = 1f
    }
}