package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye

class ConfigData {
    var currentDyeRotation : DyeRotation? = null
    var dyeOverlays : MutableMap<Dye, OverlayConfig> = mutableMapOf()

    fun overlayFor(dye: Dye): OverlayConfig =
        dyeOverlays.getOrPut(dye) {
            OverlayConfig(
                x = 0,
                y = 0,
                scale = 1f,
                toggled = false
            )
        }

    fun toggleOverlay(dye: Dye) {
        overlayFor(dye).toggled = !overlayFor(dye).toggled
    }
}

data class OverlayConfig(
    var x : Int,
    var y : Int,
    var scale : Float,
    var toggled : Boolean = false,
)