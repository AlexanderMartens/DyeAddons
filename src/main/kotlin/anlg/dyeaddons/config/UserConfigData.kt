package anlg.dyeaddons.config

import anlg.dyeaddons.gui.widgets.ProgressType

data class UserConfigData(
    var currentDyeRotation : DyeRotation? = null,
    var overlays : MutableMap<String, OverlayConfig> = mutableMapOf(),
    var progressType: ProgressType = ProgressType.TOTAL,
    var meterProgressBar : Boolean = false,
) {
    fun toggleOverlay(name: String) {
        val overlay = ConfigManager.data.config.overlays.getOrPut(name) {
            OverlayConfig(0, 0, 1f, false, Alignment.LEFT)
        }
        overlay.toggled = !overlay.toggled
    }
}

data class OverlayConfig(
    var x : Int,
    var y : Int,
    var scale : Float,
    var toggled : Boolean = false,
    var alignment : Alignment = Alignment.LEFT,
)

enum class Alignment {
    LEFT,
    CENTER,
    RIGHT,
}