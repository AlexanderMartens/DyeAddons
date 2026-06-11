package anlg.dyeaddons.gui.statistics

import anlg.dyeaddons.gui.calculators.AbstractCalculator
import anlg.dyeaddons.gui.widgets.AbstractCalcWidget
import net.minecraft.network.chat.Component
import java.text.DecimalFormat

abstract class AbstractStatistics(
    x : Int,
    y : Int,
    width : Int,
    height : Int,
    message : Component,
    widgets : Map<String, AbstractCalcWidget>
) : AbstractCalculator(
    x,
    y,
    width,
    height,
    message,
    widgets
) {
    abstract fun onSave()

    abstract fun getProgress() : Double

    override fun getOutput() : String {
        return DecimalFormat("#.##%").format(getProgress()) + " expected odds of " + message.string
    }
}