package anlg.dyeaddons.events.models

import net.minecraft.network.chat.Component

class ActionBarModifyEvent(val message: Component, val formattedText: String, val unformattedText: String, var modifiedMessage: Component = message)
