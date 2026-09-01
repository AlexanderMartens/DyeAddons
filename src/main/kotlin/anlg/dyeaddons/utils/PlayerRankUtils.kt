package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent

object PlayerRankUtils {

    private val RANK_PATTERN = Regex("""(?<rank>§.\[[^]]+] |§7)(?<name>[A-Za-z0-9_]+)§.""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        val match = RANK_PATTERN.find(event.formattedText) ?: return

        val rank = match.groups["rank"]?.value ?: return
        val name = match.groups["name"]?.value ?: return

        if (name != mc.player?.name?.string) return
        ConfigManager.data.players[mc.player?.uuid]?.playerRankText = rank
    }

}