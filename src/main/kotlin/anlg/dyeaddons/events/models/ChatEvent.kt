package anlg.dyeaddons.events.models

import net.minecraft.network.chat.Component

/**
 * Called when a chat message is received. Not triggered for cancelled messages.
 * @param message The message component received.
 * @param formattedText The formatted text of the message.
 * @param unformattedText The unformatted text of the message.
 */
class ChatEvent(val message: Component, val formattedText: String, val unformattedText: String)
