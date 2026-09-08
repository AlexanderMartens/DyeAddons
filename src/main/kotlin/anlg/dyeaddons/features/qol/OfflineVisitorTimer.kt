package anlg.dyeaddons.features.qol

import anlg.dyeaddons.data.ColorCodes.RED
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.gui.overlay.AnnouncementOverlay
import anlg.dyeaddons.settings.categories.QOL
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.TabListUtils
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents

object OfflineVisitorTimer {

    private var visitorTimer = -1

    private const val MAX_VISITORS = 5

    fun init() {
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (SkyblockUtils.getWorldName() == "Garden") {
            val visitors = TabListUtils.getLineAfter("Visitors:").getOrNull(1)?.digitToIntOrNull() ?: 0

            visitorTimer = (MAX_VISITORS - visitors) * QOL.offlineVisitorMinutes * 20 * 60 - 1
            return
        }

        if (visitorTimer == 0 && (SkyblockUtils.isInSkyblock() || QOL.offlineVisitorsOutsideSkyblock)) {
            AnnouncementOverlay.queueAnnouncement(
                Component.literal("${RED}Offline Visitors Ready"),
                60,
                SoundEvents.EXPERIENCE_ORB_PICKUP)
            ChatUtils.addLocalChatMessage("Offline visitors ready!", true)
            visitorTimer--
        }
        if (visitorTimer > 0) visitorTimer--
    }
}