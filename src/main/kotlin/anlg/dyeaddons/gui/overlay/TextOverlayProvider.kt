package anlg.dyeaddons.gui.overlay

import net.minecraft.network.chat.Component
import java.awt.Color

data class TextOverlayData(
    var lines: List<Component> = listOf(),
    val title: Component? = null,
    val color: Int = Color(255, 255, 255, 255).rgb,
)

interface TextOverlayProvider {
    var textOverlayData: TextOverlayData

    val defaultWidth: Int
    val defaultHeight: Int

    fun shouldRender(): Boolean = true
}