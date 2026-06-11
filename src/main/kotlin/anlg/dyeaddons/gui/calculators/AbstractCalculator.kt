package anlg.dyeaddons.gui.calculators

import anlg.dyeaddons.gui.widgets.AbstractCalcWidget
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import java.awt.Color

abstract class AbstractCalculator(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
    message : Component,
    val widgets : Map<String, AbstractCalcWidget>
) : AbstractWidget(
    x,
    y,
    width,
    height,
    message
), ContainerEventHandler {

    protected var scrollAmount = 0.0f
    private var focusedChild: GuiEventListener? = null
    private var dragging = false


    protected fun getContentHeight(): Int {
        return widgets.values.sumOf { widget -> widget.height }
    }

    override fun extractWidgetRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        context.enableScissor(
            x,
            y,
            x + width,
            y + height
        )

        var widgetY = y - scrollAmount.toInt()

        widgets.values.filter { widget ->
            !widget.hidden
        }
            .forEach { widget ->
            widget.setPosition(x, widgetY)
            widget.width = width

            widget.extractRenderState(
                context,
                mouseX,
                mouseY,
                partialTick
            )

            widgetY += widget.height
        }

        context.disableScissor()

        renderScrollbar(context)
    }

    override fun children(): List<GuiEventListener> {
        return widgets.values.toList()
    }

    override fun mouseClicked(
        event: MouseButtonEvent,
        doubleClick: Boolean
    ): Boolean {

        val oldFocus = focusedChild

        for (child in children()) {
            if (child.mouseClicked(event, doubleClick) && !(child as AbstractCalcWidget).hidden) {

                if (oldFocus is AbstractCalcWidget && oldFocus !== child) {
                    oldFocus.widget.isFocused = false
                }

                focusedChild = child

                child.widget.isFocused = true

                return true
            }
        }

        if (oldFocus is AbstractCalcWidget) {
            oldFocus.widget.isFocused = false
        }

        focusedChild = null

        return false
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {

        return children().any { child -> child.mouseReleased(event) }
    }

    override fun mouseDragged(
        event: MouseButtonEvent,
        dx: Double,
        dy: Double
    ): Boolean {

        return focusedChild?.mouseDragged(
            event,
            dx,
            dy
        ) ?: false
    }

    override fun isDragging(): Boolean {
        return dragging
    }

    override fun setDragging(dragging: Boolean) {
        this.dragging = dragging
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double
    ): Boolean {
        if (!isMouseOver(mouseX, mouseY)) {
            return false
        }

        scrollAmount -= verticalAmount.toFloat() * 20

        val maxScroll = getMaxScroll()

        scrollAmount = scrollAmount.coerceIn(
            0.0f,
            maxScroll.toFloat()
        )

        return true
    }

    override fun getFocused(): GuiEventListener? {
        return focusedChild
    }

    override fun setFocused(focused: GuiEventListener?) {
        focusedChild = focused
    }

    override fun isFocused(): Boolean {
        return focusedChild != null
    }

    override fun setFocused(focused: Boolean) {
        super<ContainerEventHandler>.setFocused(focused)
    }

    override fun nextFocusPath(
        navigationEvent: FocusNavigationEvent
    ): ComponentPath? {

        for (child in children()) {
            val path = child.nextFocusPath(navigationEvent)

            if (path != null) {
                return ComponentPath.path(this, path)
            }
        }

        return null
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        return focusedChild?.keyPressed(event) ?: false
    }

    override fun charTyped(event: CharacterEvent): Boolean {
        return focusedChild?.charTyped(event) ?: false
    }

    private fun renderScrollbar(
        context: GuiGraphicsExtractor
    ) {
        val contentHeight = getContentHeight()

        if (contentHeight <= height) {
            return
        }

        val thumbHeight =
            maxOf(
                20,
                (height.toFloat() * height / contentHeight).toInt()
            )

        val maxScroll = getMaxScroll()

        val thumbY = y + ((scrollAmount / maxScroll) * (height - thumbHeight)).toInt()

        context.fill(
            x + width - 5,
            thumbY,
            x + width,
            thumbY + thumbHeight,
            Color(155, 155, 155, 200).rgb
        )
    }

    protected fun getMaxScroll(): Int {
        return maxOf(
            0,
            getContentHeight() - height
        )
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {}

    abstract fun getOutput() : String

}