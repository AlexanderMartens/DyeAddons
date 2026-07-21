package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.data.Dye
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

object Overlay : HudElement {

    var registeredElements = mutableListOf<AbstractOverlay>()

    private val overlayFactories = mapOf<String, (String, OverlayConfig) -> AbstractOverlay>(
        "Rotation" to { _, config ->
            RotationOverlay(config.x, config.y, config.scale, config.toggled)
        },
        "Dye" to { name, config ->
            val dye = name.removePrefix("Dye:")
            DyePanelOverlay(config.x, config.y, config.scale, config.toggled, Dye.fromValue(dye))
        }
    )

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        registeredElements = ConfigManager.data.config.overlays.mapNotNull { (name, config) ->
            val type = name.substringBefore(':')
            overlayFactories[type]?.invoke(name, config)
        }.toMutableList()

        registeredElements.forEach { element ->
            if (element.shouldRender()) element.extractRenderState(context, deltaTracker)
        }
    }

    fun resetOverlays() {
        ConfigManager.data.config.overlays.values.forEach { element ->
            element.x = 0
            element.y = 0
            element.scale = 1f
        }
    }
}