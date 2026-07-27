package anlg.dyeaddons.utils.extensions

//? if <26.1 {
/*// 26.1 split the old immediate-mode GuiGraphics into a GuiGraphicsExtractor type; alias it back
// so the rest of the codebase doesn't need a separate type per era. Method name differences
// (text/centeredText/textWithWordWrap/outline vs. drawString/drawCenteredString/drawWordWrap/
// renderOutline) are rewritten mechanically in stonecutter.gradle.kts instead of shimmed here,
// since extension functions would need an explicit import at every call site.
typealias GuiGraphicsExtractor = net.minecraft.client.gui.GuiGraphics
*///?}
