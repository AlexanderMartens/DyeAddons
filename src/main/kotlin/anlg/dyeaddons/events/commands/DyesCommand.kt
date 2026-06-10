package anlg.dyeaddons.events.commands

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.gui.DyesScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal

object DyesCommand {

    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(literal("dyes")
                    .executes {
                        val mc = DyeAddons.mc
                        mc.execute { mc.setScreen(DyesScreen())
                        }
                        1
                    }
            )
        }
    }
}