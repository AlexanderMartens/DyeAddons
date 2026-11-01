package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.models.*
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.currentScreen
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
import net.minecraft.world.item.ItemStack
import kotlin.reflect.KClass

/**
 * Credits to Feesh
 */
object EventBus {
    private val subscribers = mutableMapOf<KClass<*>, MutableList<(Any) -> Unit>>()

    private var lastOpenedInventoryScreen: AbstractContainerScreen<*>? = null
    private var pendingInventoryScreen: AbstractContainerScreen<*>? = null
    private var lastInventorySnapshot: List<ItemStack>? = null
    private var stableTicks = 0
    private var ticksSinceOpen = 0

    private const val REQUIRED_STABLE_TICKS = 2
    private const val MINIMUM_OPEN_TICKS = 2

    private val pendingTasks = mutableListOf<() -> Unit>()

    private fun onClientTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        pendingTasks.toList().also { pendingTasks.clear() }
            .forEach { it() }
    }

    fun publish(event: Any) {
        subscribers[event::class]?.forEach { it(event) }
    }

    fun <T : Any> subscribe(eventType: KClass<T>, callback: (T) -> Unit) {
        val callbacks = subscribers.getOrPut(eventType) { mutableListOf() }
        callbacks.add(callback as (Any) -> Unit)
    }

    fun runNextTick(task: () -> Unit) {
        pendingTasks.add(task)
    }

    fun init() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            publish(ClientConnectEvent())
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            publish(ClientDisconnectEvent())
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { message, isOverlay ->
            if (isOverlay) {
                val event = ActionBarEvent(message, message.getFormattedString(), message.string.removeFormatting())
                publish(event)
                true
            } else {
                val event = ChatEvent(message, message.getFormattedString(), message.string.removeFormatting())
                publish(event)
                true
            }
        }

        ClientReceiveMessageEvents.ALLOW_GAME.register { message, isOverlay ->
            if (isOverlay) {
                val event = ActionBarCancellableEvent(message,message.getFormattedString(),message.string.removeFormatting(),false)
                publish(event)
                !event.isCancelled
            } else {
                val event = ChatCancellableEvent(message,message.getFormattedString(), message.string.removeFormatting(), false)
                publish(event)
                !event.isCancelled
            }
        }

        ClientReceiveMessageEvents.MODIFY_GAME.register { message, isOverlay ->
            if (isOverlay) {
                val event = ActionBarModifyEvent(message,message.getFormattedString(),message.string.removeFormatting(), message)
                publish(event)
                event.modifiedMessage
            } else {
                val event = ChatModifyEvent(message,message.getFormattedString(), message.string.removeFormatting(), message)
                publish(event)
                event.modifiedMessage
            }
        }

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            publish(ScreenBeforeInitEvent(screen))
        }

        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenEvents.remove(screen).register {
                if (screen === pendingInventoryScreen) {
                    pendingInventoryScreen = null
                    lastInventorySnapshot = null
                    stableTicks = 0
                    ticksSinceOpen = 0
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
                screen !== lastOpenedInventoryScreen
            ) {
                lastOpenedInventoryScreen = screen
                pendingInventoryScreen = screen

                lastInventorySnapshot = null
                stableTicks = 0
                ticksSinceOpen = 0
            }

            ScreenMouseEvents.afterMouseClick(screen).register { scr, click, consumed ->
                publish(AfterMouseClickEvent(scr, click, consumed))
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
            checkPendingInventory()
        }
        subscribe(ClientTickEvent::class, ::onClientTick)
    }

    private fun checkPendingInventory() {
        val screen = pendingInventoryScreen ?: return

        if (mc.currentScreen() !== screen) {
            pendingInventoryScreen = null
            lastInventorySnapshot = null
            return
        }

        ticksSinceOpen++

        val currentSnapshot = screen.menu.slots.map { it.item.copy() }
        val previousSnapshot = lastInventorySnapshot

        if (previousSnapshot != null &&
            currentSnapshot.size == previousSnapshot.size &&
            currentSnapshot.indices.all { i ->
                ItemStack.matches(currentSnapshot[i], previousSnapshot[i])
            }
        ) {
            stableTicks++
        } else {
            stableTicks = 0
        }

        lastInventorySnapshot = currentSnapshot

        if (ticksSinceOpen >= MINIMUM_OPEN_TICKS &&
            stableTicks >= REQUIRED_STABLE_TICKS
        ) {
            pendingInventoryScreen = null
            lastInventorySnapshot = null
            publish(InventoryOpenEvent(screen))
        }
    }
}
