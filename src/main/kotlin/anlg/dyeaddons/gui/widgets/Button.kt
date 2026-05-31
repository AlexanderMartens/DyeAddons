package anlg.dyeaddons.gui.widgets

import net.minecraft.client.gui.GuiGraphicsExtractor
import java.awt.Color

abstract class Button (
    val width : Int,
    val height : Int,
    val x : Int,
    val y : Int,
    val padding : Int = 0){

    open fun draw(context : GuiGraphicsExtractor, mouseX : Int, mouseY : Int) {

        context.fill(
            x + padding,
            y + padding,
            x + width - padding,
            y + height - padding,
            if (isHovered(mouseX, mouseY)) {
                Color(166, 166, 166, 100).rgb
            } else {
                Color(133, 133, 133, 100).rgb
            }
        )
    }

    fun isHovered(mouseX: Int, mouseY: Int): Boolean {
        return (x..(x+width)).contains(mouseX) && (y..(y+height)).contains(mouseY)
    }

    abstract fun onClick(mouseX: Int, mouseY: Int)
}