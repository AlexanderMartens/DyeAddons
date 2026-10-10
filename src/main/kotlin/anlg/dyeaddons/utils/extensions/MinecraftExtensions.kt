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
    extractRenderState(context, deltaTracker)
}

/** Same as [HudElement.renderElement] but for AbstractWidget's own render dispatcher. */
fun AbstractWidget.renderElement(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
    extractRenderState(context, mouseX, mouseY, partialTick)
}

/**
 * 26.2 moved screen/chat ownership from [Minecraft] onto `Minecraft.gui` (and further onto
 * `Gui.hud` for chat). These shims keep call sites version-agnostic.
 */
fun Minecraft.currentScreen(): Screen? {
    //? if >=26.2 {
    return gui.screen()
    //?} else {
    /*return screen
    *///?}
}

fun Minecraft.openScreen(screen: Screen?) {
    //? if >=26.2 {
    gui.setScreen(screen)
    //?} else {
    /*setScreen(screen)
    *///?}
}

val Minecraft.chatComponent: ChatComponent
    get() {
        //? if >=26.2 {
        return gui.hud.chat
        //?} else {
        /*return gui.chat
        *///?}
    }

/**
 * 26.3 has different openUri and openPath
 */
fun openUriCompat(url: String) {
    //? if >=26.3 {
    com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(url))
    //?} else {
    /*net.minecraft.util.Util.getPlatform().openUri(url)
    *///?}
}

fun openPathCompat(path: java.nio.file.Path) {
    //? if >=26.3 {
    com.mojang.blaze3d.Blaze3D.openPath(path)
    //?} else {
    /*net.minecraft.util.Util.getPlatform().openUri(path.toUri().toString())
    *///?}
}