package anlg.dyeaddons.features.qol

import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.data.SkyblockZone
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.EntitySpawnEvent
import anlg.dyeaddons.events.models.ServerTickEvent
import anlg.dyeaddons.gui.overlay.OverlayRegistry
import anlg.dyeaddons.gui.overlay.TextOverlayData
import anlg.dyeaddons.gui.overlay.TextOverlayProvider
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.SkyblockUtils.isInZone
import anlg.dyeaddons.utils.SoundUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.monster.Ghast

object HellwispTimer : TextOverlayProvider {

    private var hellwispTimer = -1

    private val inHellwispZone: Boolean
        get() = SkyblockUtils.isInSkyblock() &&
                SkyblockUtils.getWorldName() == "Crimson Isle" &&
                SkyblockUtils.isInZone(SkyblockZone.HELLWISP)


    override var textOverlayData = TextOverlayData()
    override val defaultWidth: Int = 150
    override val defaultHeight: Int = 13
    override val textScale: Float = 1f

    override fun shouldRender(): Boolean {
        return inHellwispZone
    }

    fun init() {
        OverlayRegistry.textProviders["Hellwisp Timer"] = this
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
        EventBus.subscribe(EntitySpawnEvent::class, ::onEntitySpawn)
        EventBus.subscribe(ServerTickEvent::class, ::onServerTick)
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!inHellwispZone) return

        textOverlayData.title = Component.literal("Hellwisp: " +
            when (hellwispTimer) {
                in 100..300 -> "${YELLOW}${"%.2f".format(hellwispTimer / 20f)}s"
                in 40..100 -> "${GOLD}${"%.2f".format(hellwispTimer / 20f)}s"
                in 0..40 -> "${RED}${"%.2f".format(hellwispTimer / 20f)}s"
                else -> "${RED}Soon"
            }
        )
    }

    private fun onEntitySpawn(event: EntitySpawnEvent) {
        if (!inHellwispZone) return

        if (event.entity !is Ghast || !event.entity.isInZone(SkyblockZone.HELLWISP)) return

        if (hellwispTimer < 10 * 20) {
            hellwispTimer = 15 * 20
            SoundUtils.playSound()
        }
    }

    private fun onServerTick(@Suppress("UNUSED_PARAMETER") event: ServerTickEvent) {
        if (!inHellwispZone) return

        if (hellwispTimer > -1) hellwispTimer--
    }

}