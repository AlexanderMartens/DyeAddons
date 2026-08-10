package anlg.dyeaddons.gui.overlay

import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.OverlayConfig
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.settings.categories.General
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.SoundUtils
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import java.util.LinkedList
import java.util.Queue

object AnnouncementOverlay: TextOverlayProvider {

    data class AnnouncementMessage(
        val message: Component,
        val duration: Int
    )

    override var textOverlayData: TextOverlayData = TextOverlayData()
    override val defaultWidth: Int = 300
    override val defaultHeight: Int = 40
    override val textScale: Float = 2f

    private val messageQueue: Queue<AnnouncementMessage> = LinkedList()

    private var currentMessage: Component? = null
    private var currentDuration: Int = 0

    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock()
    }

    fun init() {
        OverlayRegistry.textProviders["Announcement"] = this
        EventBus.subscribe(ClientTickEvent::class, ::onTick)

        ConfigManager.data.config.overlays.getOrPut("Text:Announcement") {
            OverlayConfig(0, 0, 1f, true)
        }
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!SkyblockUtils.isInSkyblock()) return
        textOverlayData.title = currentMessage

        if (currentDuration > 0) currentDuration--

        if (currentDuration == 0) {
            currentMessage = null
        }

        if (currentDuration != 0 || messageQueue.isEmpty()) return

        val nextAnnouncement = messageQueue.poll()
        currentMessage = nextAnnouncement.message
        currentDuration = nextAnnouncement.duration

    }

    /**
     * Adds a message to the announcement queue.
     * @param message The text of the announcement
     * @param duration How many ticks the announcement lasts
     * @param sound Optional sound to be played when receiving the announcement, null is no sound
     */
    fun queueAnnouncement(message: Component, duration: Int, sound: SoundEvent? = null) {
        if (!General.announcementToggle) return
        messageQueue.offer(AnnouncementMessage(message, duration))
        sound?.let {
            SoundUtils.playSound(it)
        }
    }
}