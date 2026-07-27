package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.utils.extensions.openScreen
//? if <26.2 {
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
//?} else {
/*import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
*///?}

//? if <26.2 {
object General : CategoryKt("General") {

    init {
        button {
            title = "Open dye compendium"
            description = "Opens the /dyeaddons compendium menu"
            text = "Open"
            onClick {
                mc.openScreen(DyesScreen())
            }
        }

        button {
            title = "Move overlays"
            description = "Opens the screen to move overlays"
            text = "Open"
            onClick {
                mc.openScreen(MoveOverlaysScreen())
            }
        }

        button {
            title = "Reset Overlays"
            description = "Resets the position and scale of all overlays"
            text = "Reset"
            onClick {
                Overlay.resetOverlays()
            }
        }
    }

    var rotationOverlayToggle by ObservableEntry(
        boolean(ConfigManager.data.config.overlays["Rotation"]?.toggled ?: false) {
            this.name = Translated("Toggle rotation overlay")
            this.description = Translated("Adds/removes the rotation overlay to your screen")
        }
    ) { _, new ->
        if ((ConfigManager.data.config.overlays["Rotation"]?.toggled ?: false) != new) {
            ConfigManager.data.config.toggleOverlay("Rotation")
        }
    }
}
//?} else {
/*
// 26.2 has no resourcefulconfigkt build yet - this is the plain annotation-based Java API
// (config.wiki.teamresourceful.com) instead of the Kotlin DSL used on other versions.
// rotationOverlayToggle has no on-change callback here, so DyeAddons.onInitializeClient polls
// it against ConfigManager on a tick (see General.syncOverlayToggle()).
@Category("General")
class General {
    companion object {
        @JvmField
        @ConfigButton(title = "Open dye compendium", text = "Open")
        val openCompendium: Runnable = Runnable { mc.openScreen(DyesScreen()) }

        @JvmField
        @ConfigButton(title = "Move overlays", text = "Open")
        val moveOverlays: Runnable = Runnable { mc.openScreen(MoveOverlaysScreen()) }

        @JvmField
        @ConfigButton(title = "Reset Overlays", text = "Reset")
        val resetOverlaysButton: Runnable = Runnable { Overlay.resetOverlays() }

        @JvmField
        @ConfigEntry(id = "rotationOverlayToggle", translation = "dyeaddons.config.rotationOverlayToggle.name")
        @Comment(value = "", translation = "dyeaddons.config.rotationOverlayToggle.comment")
        var rotationOverlayToggle: Boolean = ConfigManager.data.config.overlays["Rotation"]?.toggled ?: false

        init {
            EventBus.subscribe(ClientTickEvent::class) {
                val current = ConfigManager.data.config.overlays["Rotation"]?.toggled ?: false
                if (current != rotationOverlayToggle) {
                    ConfigManager.data.config.toggleOverlay("Rotation")
                }
            }
        }
    }
}
*///?}
