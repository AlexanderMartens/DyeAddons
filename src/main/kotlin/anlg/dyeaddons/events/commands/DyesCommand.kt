package anlg.dyeaddons.events.commands

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.utils.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal

object DyesCommand {

    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(literal("dyes")
                    .executes {
                        mc.execute { mc.setScreen(DyesScreen()) }
                        1
                    }
                .then(literal("gui")
                    .executes {
                        mc.execute { mc.setScreen(MoveOverlaysScreen()) }
                        1
                    }
                )
                .then(literal("toggleRotationOverlay")
                    .executes {
                        ConfigManager.data.config.rotationOverlay.toggled = !ConfigManager.data.config.rotationOverlay.toggled
                        1
                    }
                )
                .then(literal("toggleDebugMode")
                    .executes {
                        DyeAddons.debugMode = !DyeAddons.debugMode
                        ChatUtils.addLocalChatMessage("Debug mode set to ${DyeAddons.debugMode}", true)
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