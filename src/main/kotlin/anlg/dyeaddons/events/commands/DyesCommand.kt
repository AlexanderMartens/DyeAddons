package anlg.dyeaddons.events.commands

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
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
            )
        }
    }
}