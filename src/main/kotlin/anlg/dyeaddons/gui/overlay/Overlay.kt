package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.Alignment
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.AfterMouseClickEvent
import anlg.dyeaddons.utils.extensions.currentScreen
import anlg.dyeaddons.utils.extensions.renderElement
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen

object OverlayRegistry {
    val textProviders = mutableMapOf<String, TextOverlayProvider>()
}

object Overlay : HudElement {

    var registeredElements = mutableMapOf<String, AbstractOverlay>()

    private val overlayFactories = mapOf<String, (String, OverlayConfig) -> AbstractOverlay?>(
        "Rotation" to { _, config ->
            RotationOverlay(config.x,
                config.y,
                config.scale,
                config.toggled,
                config.alignment ?: Alignment.LEFT)
        },
        "Dye" to { name, config ->
            val dye = name.removePrefix("Dye:")
            DyePanelOverlay(config.x,
                config.y,
                config.scale,
                config.toggled,
                config.alignment ?: Alignment.LEFT,
                Dye.fromValue(dye))
        },
        "Text" to { name, config ->
            val textProvider = OverlayRegistry.textProviders[name.removePrefix("Text:")]
            if (textProvider == null) {
                null
            } else {
                TextOverlay(name.removePrefix("Text:"),
                    config.x,
                    config.y,
                    config.scale,
                    config.toggled,
                    config.alignment ?: Alignment.LEFT,
                    textProvider,
                    textProvider.defaultWidth,
                    textProvider.defaultHeight)
            }
        }
    )

    init {
        EventBus.subscribe(AfterMouseClickEvent::class, ::onMouseClick)
    }

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        registeredElements.values.forEach { element ->
            if (element.shouldRender()) {
                element.renderElement(context, deltaTracker)
            }
        }
    }

    private fun onMouseClick(event: AfterMouseClickEvent) {
        if (mc.currentScreen() !is InventoryScreen && mc.currentScreen() !is ChatScreen) return
        if (event.event.button() != 0) return

        registeredElements.values.filter { it.shouldRender() }.forEach { element ->
            val localX = (event.event.x - element.leftEdge) / element.scale
            val localY = (event.event.y - element.y) / element.scale
            element.onClick(localX, localY)
        }
    }

    fun refreshOverlays() {
        val configs = ConfigManager.data.config.overlays

        registeredElements.keys.removeIf { name ->
            val config = configs[name]
            config == null || !config.toggled
        }

        configs.forEach { (name, config) ->
            if (!config.toggled) return@forEach

            val existing = registeredElements[name]

            if (existing != null) {
                existing.updateConfig(config)
            } else {
                val type = name.substringBefore(':')
                overlayFactories[type]?.invoke(name, config)?.let {
                    registeredElements[name] = it
                }
            }
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