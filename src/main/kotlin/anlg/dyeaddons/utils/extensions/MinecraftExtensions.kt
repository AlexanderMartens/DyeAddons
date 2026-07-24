package anlg.dyeaddons.utils.extensions

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.screens.Screen

/**
 * 26.1 renamed HudElement's own render method too (extractRenderState vs. render). Kept as a
 * global replace target elsewhere would risk corrupting unrelated text containing "render", so
 * this is the one place that calls into it by an unstable name.
 */
fun HudElement.renderElement(context: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
    //? if >=26.1 {
    extractRenderState(context, deltaTracker)
    //?} else
    /*render(context, deltaTracker)*/
}

/** Same as [HudElement.renderElement] but for AbstractWidget's own render dispatcher. */
fun AbstractWidget.renderElement(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
    //? if >=26.1 {
    extractRenderState(context, mouseX, mouseY, partialTick)
    //?} else
    /*render(context, mouseX, mouseY, partialTick)*/
}

/**
 * 26.2 moved screen/chat ownership from [Minecraft] onto `Minecraft.gui` (and further onto
 * `Gui.hud` for chat). These shims keep call sites version-agnostic.
 */
fun Minecraft.currentScreen(): Screen? {
    //? if >=26.2 {
    /*return gui.screen()
    *///?} else
    return screen
}

fun Minecraft.openScreen(screen: Screen?) {
    //? if >=26.2 {
    /*gui.setScreen(screen)
    *///?} else
    setScreen(screen)
}

val Minecraft.chatComponent: ChatComponent
    get() {
        //? if >=26.2 {
        /*return gui.hud.chat
        *///?} else
        return gui.chat
    }
