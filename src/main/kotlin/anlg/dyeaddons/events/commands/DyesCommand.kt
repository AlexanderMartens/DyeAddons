package anlg.dyeaddons.events.commands

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.settings.categories.General
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal

object DyesCommand {

    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(literal("dyeaddons")
                    .executes {
                        mc.execute { mc.setScreen(DyesScreen()) }
                        1
                    }
                .then(literal("compendium")
                    .executes {
                        mc.execute { mc.setScreen(DyesScreen()) }
                        1
                    }
                )
                .then(literal("config")
                    .executes {
                        mc.execute { mc.setScreen(ResourcefulConfigScreen.getFactory("dyeaddons").apply(null)) }
                        1
                    }
                )
                .then(literal("gui")
                    .executes {
                        mc.execute { mc.setScreen(MoveOverlaysScreen()) }
                        1
                    }
                )
                .then(literal("toggleRotationOverlay")
                    .executes {
                        ConfigManager.data.config.toggleOverlay("Rotation")
                        General.rotationOverlayToggle = ConfigManager.data.config.overlays["Rotation"]?.toggled ?: false
                        1
                    }
                )
                .then(literal("resetOverlays")
                    .executes {
                        Overlay.resetOverlays()
                        1
                    }
                )
            )
        }
    }
}