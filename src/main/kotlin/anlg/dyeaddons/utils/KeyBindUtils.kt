package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.gui.overlay.MoveOverlaysScreen
import anlg.dyeaddons.utils.extensions.openScreen
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW

object KeyBindUtils {

    private val keybindCallbacks = mutableListOf<Pair<KeyMapping, () -> Unit>>()
    private val DYEADDONS_CATEGORY = KeyMapping.Category(Identifier.fromNamespaceAndPath("dyeaddons", "keybinds"))
    private var keybindsRegistered = false

    fun init() {
        registerAllKeyBinds()
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun registerAllKeyBinds() {
        if (keybindsRegistered) return

        registerKeyBind("key.dyeaddons.moveOverlays", GLFW.GLFW_KEY_UNKNOWN) {
            mc.execute { mc.openScreen(MoveOverlaysScreen()) }
        }

        keybindsRegistered = true
    }

    private fun registerKeyBind(id: String, keyCode: Int = GLFW.GLFW_KEY_UNKNOWN, callback: () -> Unit): KeyMapping {
        val keyBinding = KeyMapping(
            id,
            InputConstants.Type.KEYSYM,
            keyCode,
            DYEADDONS_CATEGORY
        )
        KeyMappingHelper.registerKeyMapping(keyBinding)
        keybindCallbacks.add(keyBinding to callback)
        return keyBinding
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        keybindCallbacks.forEach { (keyBinding, callback) ->
            if (keyBinding.consumeClick()) {
                callback()
            }
        }
    }
}