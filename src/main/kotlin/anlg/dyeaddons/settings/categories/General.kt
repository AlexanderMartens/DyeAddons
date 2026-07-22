package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry

object General : CategoryKt("General") {

    init {
        button {
            title = "Open dye compendium"
            description = "Opens the /dyeaddons compendium menu"
            text = "Open"
            onClick {
                mc.setScreen(DyesScreen())
            }
        }

        button {
            title = "Move overlays"
            description = "Opens the screen to move overlays"
            text = "Open"
            onClick {
                mc.setScreen(MoveOverlaysScreen())
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