package anlg.dyeaddons.events

import anlg.dyeaddons.events.models.ActionBarCancellableEvent
import anlg.dyeaddons.events.models.ActionBarEvent
import anlg.dyeaddons.events.models.AfterMouseClickEvent
import anlg.dyeaddons.events.models.ArmorStandDespawnedEvent
import anlg.dyeaddons.events.models.ArmorStandLoadedEvent
import anlg.dyeaddons.events.models.ChatCancellableEvent
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.ClientConnectEvent
import anlg.dyeaddons.events.models.ClientDisconnectEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.EntityDespawnEvent
import anlg.dyeaddons.events.models.EntitySpawnEvent
import anlg.dyeaddons.events.models.GameClosedEvent
import anlg.dyeaddons.events.models.GameStartedEvent
import anlg.dyeaddons.events.models.GuiClosedEvent
import anlg.dyeaddons.events.models.InventoryOpenEvent
import anlg.dyeaddons.events.models.ItemEntityLoadedEvent
import anlg.dyeaddons.events.models.ScreenBeforeInitEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import anlg.dyeaddons.utils.SkyblockUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.ItemEntity
import kotlin.reflect.KClass

/**
 * Credits to Feesh
 */
object EventBus {
    private val subscribers = mutableMapOf<KClass<*>, MutableList<(Any) -> Unit>>()
    private var lastOpenedInventoryScreen: AbstractContainerScreen<*>? = null
    private var pendingInventoryScreen: AbstractContainerScreen<*>? = null

    fun publish(event: Any) {
        subscribers[event::class]?.forEach { it(event) }
    }

    fun <T : Any> subscribe(eventType: KClass<T>, callback: (T) -> Unit) {
        val callbacks = subscribers.getOrPut(eventType) { mutableListOf() }
        callbacks.add(callback as (Any) -> Unit)
    }

    fun init() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            publish(ClientConnectEvent())
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            publish(ClientDisconnectEvent())
        }

        ClientReceiveMessageEvents.GAME.register { message, isOverlay ->
            if (isOverlay) {
                publish(ActionBarEvent(message, message.getFormattedString(), message.string.removeFormatting() ?: ""))
            } else {
                publish(ChatEvent(message, message.getFormattedString(), message.string.removeFormatting() ?: ""))
            }
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { message, isOverlay ->
            if (isOverlay) {
                val event = ActionBarCancellableEvent(message,message.getFormattedString(),message.string.removeFormatting() ?: "",false)
                publish(event)
                !event.isCancelled
            } else {
                val event = ChatCancellableEvent(message,message.getFormattedString(), message.string.removeFormatting() ?: "", false)
                publish(event)
                !event.isCancelled
            }
        }

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            publish(ScreenBeforeInitEvent(screen))
        }

        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenEvents.remove(screen).register {
                if (screen === lastOpenedInventoryScreen) {
                    lastOpenedInventoryScreen = null
                }

                val guiName = when (screen) {
                    is ChatScreen -> "Chat"
                    is InventoryScreen -> "Inventory"
                    is AbstractContainerScreen<*> -> screen.title.string
                    else -> screen.javaClass.getSimpleName()
                }
                publish(GuiClosedEvent(guiName))
            }

            if (screen is AbstractContainerScreen<*> &&
                screen !is InventoryScreen &&
                SkyblockUtils.isInSkyblock() &&
                screen !== lastOpenedInventoryScreen) {
                lastOpenedInventoryScreen = screen
                pendingInventoryScreen = screen
            }

            ScreenMouseEvents.afterMouseClick(screen).register { scr, click, consumed ->
                publish(AfterMouseClickEvent(scr, click.x(), click.y(), click.buttonInfo().button))
                consumed
            }
        }
        
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            publish(ClientTickEvent(client))
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            publish(GameClosedEvent())
        }

        ClientLifecycleEvents.CLIENT_STARTED.register { _ ->
            publish(GameStartedEvent())
        }

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register { minecraft, level ->
            publish(WorldChangedEvent(minecraft, level))
        }

        ClientEntityEvents.ENTITY_LOAD.register { entity, _ ->
            when (entity) {
                is ItemEntity -> publish(ItemEntityLoadedEvent(entity))
                is ArmorStand -> if (entity.isAlive) publish(
                    ArmorStandLoadedEvent(entity)
                )
                else -> publish(EntitySpawnEvent(entity))
            }
        }

        ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
            if (entity is ArmorStand) {
                publish(ArmorStandDespawnedEvent(entity))
            } else {
                publish(EntityDespawnEvent(entity))
            }
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            val screen = pendingInventoryScreen ?: return@register

            if (isInventoryLoaded(screen)) {
                pendingInventoryScreen = null
                publish(InventoryOpenEvent(screen))
            }
        }
    }

    private fun isInventoryLoaded(
        screen: AbstractContainerScreen<*>
    ): Boolean {
        return screen.menu.slots.any {
            !it.item.isEmpty
        }
    }
}
