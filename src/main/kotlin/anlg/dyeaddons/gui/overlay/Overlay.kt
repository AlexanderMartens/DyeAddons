package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.AfterMouseClickEvent
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen

object Overlay : HudElement {

    var registeredElements = mutableListOf<AbstractOverlay>()

    init {
        EventBus.subscribe(AfterMouseClickEvent::class, ::onMouseClick)
    }

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

    private fun onMouseClick(event: AfterMouseClickEvent) {
        if (mc.screen !is InventoryScreen && mc.screen !is ChatScreen) return
        if (event.event.button() != 0) return

        registeredElements.filter { it.shouldRender() }.forEach { element ->
            val localX = (event.event.x - element.x) / element.scale
            val localY = (event.event.y - element.y) / element.scale
            element.onClick(localX, localY)
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