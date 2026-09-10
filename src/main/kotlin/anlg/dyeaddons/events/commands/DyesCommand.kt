package anlg.dyeaddons.events.commands

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.features.dye.MedalIntegration
import anlg.dyeaddons.features.qol.QuickStart
import anlg.dyeaddons.gui.DyesScreen
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.gui.overlay.Overlay
import anlg.dyeaddons.settings.categories.General
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SoundUtils
import anlg.dyeaddons.utils.extensions.openScreen
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

object DyesCommand {

    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(literal("dyeaddons")
                    .executes {
                        mc.execute { mc.openScreen(DyesScreen()) }
                        1
                    }
                .then(literal("compendium")
                    .executes {
                        mc.execute { mc.openScreen(DyesScreen()) }
                        1
                    }
                )
                .then(literal("config")
                    .executes {
                        mc.execute { mc.openScreen(ResourcefulConfigScreen.getFactory("dyeaddons").apply(null)) }
                        1
                    }
                )
                .then(literal("gui")
                    .executes {
                        mc.execute { mc.openScreen(MoveOverlaysScreen()) }
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
                .then(literal("reloadsounds")
                    .executes {
                        SoundUtils.reload()
                        ChatUtils.addLocalChatMessage("Reloaded custom sounds.", true)
                        1
                    }
                )
                .then(literal("medaltest")
                    .then(ClientCommands.argument("dye", StringArgumentType.greedyString())
                        .executes { context ->
                            val dye = Dye.fromValue(StringArgumentType.getString(context, "dye"))
                            if (dye != null) {
                                MedalIntegration.testClip(dye)
                            }
                            1
                        }
                    )
                )
                .then(literal("quickstart")
                    .then(ClientCommands.argument("magic find", FloatArgumentType.floatArg(0f))
                        .then(ClientCommands.argument("looting", IntegerArgumentType.integer(0, 5))
                            .then(ClientCommands.argument("overbloom", FloatArgumentType.floatArg(0f))
                                .executes { context ->
                                    val magicFind = FloatArgumentType.getFloat(context, "magic find")
                                    val looting = IntegerArgumentType.getInteger(context, "looting")
                                    val overbloom = FloatArgumentType.getFloat(context, "overbloom")
                                    ChatUtils.addLocalChatMessage(
                                        Component.literal("${RED}${BOLD}Warning! " +
                                                "${RESET}This will overwrite your dye statistics and progress. " +
                                                "Only do this if you just downloaded the mod or your data has been corrupted. ")
                                            .append(Component.literal("${RED}${BOLD}[Confirm]")
                                                .withStyle { it.withHoverEvent(
                                                    HoverEvent.ShowText(
                                                    Component.literal("${YELLOW}Click here to run /dyeaddons quickstart $magicFind $looting $overbloom confirm")
                                                ))}
                                                .withStyle { it.withClickEvent(
                                                    ClickEvent.RunCommand("/dyeaddons quickstart $magicFind $looting $overbloom confirm")
                                                )}),
                                        true)
                                    1
                                }
                                .then(literal("confirm")
                                    .executes { context ->
                                        val magicFind = FloatArgumentType.getFloat(context, "magic find")
                                        val looting = IntegerArgumentType.getInteger(context, "looting")
                                        val overbloom = FloatArgumentType.getFloat(context, "overbloom")
                                        QuickStart.quickStart(magicFind, looting, overbloom)
                                        1
                                    }
                                )
                            )
                        )
                    )
                )
                .then(literal("testchatmessage")
                    .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                        .executes { context ->
                            val message = StringArgumentType.getString(context, "message")

                            ChatUtils.addLocalChatMessage(message)
                            //EventBus.publish(ChatEvent(
                            //    Component.literal(message),
                            //    message,
                            //    message.removeFormatting()))
                            1
                        }
                    )
                )
            )
        }
    }
}