package anlg.dyeaddons.settings.categories

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.utils.SoundUtils
import anlg.dyeaddons.utils.extensions.openScreen
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import net.minecraft.util.Util

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

        button {
            title = "Open Custom Sound Directory"
            description = "Opens the folder to put custom sounds in. Do /dyeaddons reloadsounds after putting your sound in."
            text = "Open"
            onClick {
                val dir = SoundUtils.configSoundDirectory.toFile()
                if (!dir.exists()) dir.mkdirs()
                Util.getPlatform().openUri(dir.toURI().toString())
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

    var soundMode by boolean(true) {
        this.name = Translated("Enable sounds")
        this.description = Translated("Enables sounds played by this mod")
    }

    var announcementToggle by ObservableEntry(
        boolean(ConfigManager.data.config.overlays["Text:Announcement"]?.toggled ?: true) {
            this.name = Translated("Enable Announcements")
            this.description = Translated("Plays announcements messages and sounds")
        }
    ) { _, new ->
        val overlay = ConfigManager.data.config.overlays.getOrPut("Text:Announcement") {
            OverlayConfig(0, 0, 1f, true)
        }

        overlay.toggled = new
    }
}