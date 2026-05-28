package anlg.dyeaddons.events.commands

import anlg.dyeaddons.gui.DyesScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal

object DyesCommand {

    fun initialize() {

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

            dispatcher.register(
                literal("dyes")
                    .executes { context ->
                        val client = context.source.client

                        client.execute {
                            client.setScreen(
                                DyesScreen()
                            )
                        }

                        1
                    }
            )
        }
    }
}