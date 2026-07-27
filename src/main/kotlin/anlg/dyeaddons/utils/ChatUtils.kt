package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.extensions.chatComponent
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

object ChatUtils {

    val MOD_PREFIX = "${GRAY}[${RED}DyeAddons${GRAY}]"
    val DEBUG_PREFIX = "${GRAY}[${RED}DyeAddons Debug${GRAY}]"

    /**
     * Sends a message to the client that only the client can see
     * @param message The message to send.
     */
    fun addLocalChatMessage(message: String, withPrefix: Boolean = false) {
        if (message.isEmpty()) return
        val formattedMessage = if (withPrefix) "${MOD_PREFIX} ${RESET}${message}" else message
        DyeAddons.mc.chatComponent.addClientSystemMessage(Component.literal(formattedMessage))
    }

    /**
     * Sends a chat message to the client if they are in debug mode
     */
    fun addDebugChatMessage(message: String, category: DebugCategories = DebugCategories.OTHER) {
        if (message.isEmpty()) return
        val formattedMessage = "${DEBUG_PREFIX} ${GRAY}(${category.displayName}${GRAY}) ${RESET}${message}"
        DyeAddons.mc.chatComponent.addClientSystemMessage(Component.literal(formattedMessage))
    }

    /**
     * Removes all formatting from a string
     */
    fun String.removeFormatting(): String {
        if (this.isEmpty()) return ""
        return this.replace(Regex("§."), "")
    }

    /**
     * Get a string with color and formatting codes.
     * Credits to SkyblockOverhaul
     */
    fun Component.getFormattedString(): String {
        val builder = StringBuilder()

        this.visit(
            { style, str ->
                builder.append(style.getFormatting())
                builder.append(str)
                Optional.empty<Any>()
            },
            Style.EMPTY
        )
        return builder.toString()
    }

    /**
     * Get color and formatting codes for the Style, e.g. §b§l
     */
    private fun Style.getFormatting() = buildString {
        val color = this@getFormatting.color
        if (color != null) getColorCode(color)?.let { append(it) }

        val formatting = when {
            this@getFormatting.isBold -> "§l"
            this@getFormatting.isItalic -> "§o"
            this@getFormatting.isUnderlined -> "§n"
            this@getFormatting.isStrikethrough -> "§m"
            this@getFormatting.isObfuscated -> "§k"
            else -> ""
        }
        append(formatting)
    }

    private fun getColorCode(color: TextColor): String? = colorToChar[color]?.toString()

    private val colorToChar: Map<TextColor, ChatFormatting> = ChatFormatting.entries.mapNotNull { format ->
        TextColor.fromLegacyFormat(format)?.let { it to format }
    }.toMap()

}