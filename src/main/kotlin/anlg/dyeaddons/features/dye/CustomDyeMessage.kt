package anlg.dyeaddons.features.dye

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatModifyEvent
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object CustomDyeMessage {

    private val DYE_PATTERN = Regex("""§d§lWOW! (?<player>(?:§.\[[^]]+]\s)?(?<name>[A-Za-z0-9_]+))§f §6found (?<an>an?) (?<dye>§.[A-Za-z ]+) Dye(?: #[\d,]+)?§6!""")

    fun init() {
        EventBus.subscribe(ChatModifyEvent::class, ::onModifyChat)
    }

    private fun onModifyChat(event: ChatModifyEvent) {
        if (!Dyes.customDyeMessageToggle || !SkyblockUtils.isInSkyblock()) return
        val match = DYE_PATTERN.matchEntire(event.formattedText.trim()) ?: return

        val player = match.groups["player"]?.value ?: return
        val name = match.groups["name"]?.value ?: return
        if (name != Minecraft.getInstance().player?.name?.string) return
        val dyeName = match.groups["dye"]?.value ?: return
        val an = match.groups["an"]?.value ?: return

        val dye = try {
            Dye.fromValue(dyeName.removeFormatting())
        } catch(_: IllegalArgumentException) {
            DyeAddons.debug("Could not parse dye: ${dyeName.removeFormatting()}")
            return
        }

        val dropped = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dropped ?: 0
        val progress = ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress ?: 0.0
        val sinceLast = progress - (ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.dyesDropped?.maxByOrNull{ it.progress }?.progress ?: 0.0)

        val customDyeMessage = Dyes.customDyeMessage[0].trim()
            .replace("{player}", player)
            .replace("{dye}", dyeName)
            .replace("{dropped}", dropped.toString())
            .replace("{progress}",  "%.2f".format(progress * 100.0) + "%")
            .replace("{since}", "%.2f".format(sinceLast * 100.0) + "%")
            .replace("{an}", an)
            .replace("&", "§")

        event.modifiedMessage = Component.literal(customDyeMessage)
    }
}