package anlg.dyeaddons.events.models

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent

data class AfterMouseClickEvent(
    val screen: Screen,
    val event: MouseButtonEvent,
    val doubleClick: Boolean
)
