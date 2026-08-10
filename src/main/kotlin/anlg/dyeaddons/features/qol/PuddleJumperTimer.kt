package anlg.dyeaddons.features.qol

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.data.ColorCodes.*
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.EntityDespawnEvent
import anlg.dyeaddons.events.models.EntitySpawnEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.events.models.ServerTickEvent
import anlg.dyeaddons.events.models.WorldChangedEvent
import anlg.dyeaddons.gui.overlay.AnnouncementOverlay
import anlg.dyeaddons.gui.overlay.TextOverlayData
import anlg.dyeaddons.gui.overlay.TextOverlayProvider
import anlg.dyeaddons.gui.overlay.OverlayRegistry
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.QOL
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.animal.frog.Frog

object PuddleJumperTimer : TextOverlayProvider {

    data class TrackedFrog(
        val entity: Frog,
        val timestamp: Int,
    )

    data class TrackedPuddleJumper(
        val frog: TrackedFrog,
        var jumpStage: Int = 0, // 0 is when it first spawns, increments when it starts/stops jumping, odd = jumping, even = not jumping, dies a few seconds after stage 8
        var deathTimer: Int = 0,
        val tickStages: MutableList<Int> = mutableListOf(),
        var playedAnnouncement: Boolean = false,
    )

    private val PUDDLE_JUMPER_MESSAGE = Regex("""A Puddle Jumper is preparing for liftoff—cast your rod into it and hold on tight!""")

    private val frogs = mutableMapOf<Int, TrackedFrog>()
    private val puddleJumpers = mutableMapOf<Int, TrackedPuddleJumper>()
    private var fishedPuddleJumper = false

    private var tickCounter = 0
    private const val TICK_WINDOW = 1

    override var textOverlayData = TextOverlayData()
    override val defaultWidth: Int = 150
    override val defaultHeight: Int = 13
    override val textScale: Float = 1f
    override fun shouldRender(): Boolean {
        return SkyblockUtils.isInSkyblock() && SkyblockUtils.getWorldName() == "Lotus Atoll"
    }

    fun init() {
        OverlayRegistry.textProviders["Puddle Jumper"] = this
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(EntitySpawnEvent::class, ::onEntitySpawn)
        EventBus.subscribe(EntityDespawnEvent::class, ::onEntityDespawn)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
        EventBus.subscribe(ServerTickEvent::class, ::onServerTick)
        EventBus.subscribe(WorldChangedEvent::class, ::onWorldChange)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.isInSkyblock() || SkyblockUtils.getWorldName() != "Lotus Atoll") return
        if (!PUDDLE_JUMPER_MESSAGE.matches(event.unformattedText.trim())) return
        fishedPuddleJumper = true
    }

    private fun onEntitySpawn(event: EntitySpawnEvent) {
        if (!SkyblockUtils.isInSkyblock() || SkyblockUtils.getWorldName() != "Lotus Atoll") return
        val frog = event.entity as? Frog ?: return

        frogs[event.entity.id] = TrackedFrog(frog, tickCounter)
    }

    private fun onEntityDespawn(event: EntityDespawnEvent) {
        if (!SkyblockUtils.isInSkyblock() || SkyblockUtils.getWorldName() != "Lotus Atoll") return

        frogs.remove(event.entity.id)
        if (puddleJumpers.containsKey(event.entity.id)) {
            DyeAddons.debug("Puddle Jumper Died at $tickCounter ticks, Predicted: ${puddleJumpers[event.entity.id]?.deathTimer}", DebugCategories.KILL_EVENT)
            EventBus.publish(MobKillEvent("Puddle Jumper", 0.0,null))
        }
        puddleJumpers.remove(event.entity.id)
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!SkyblockUtils.isInSkyblock() || SkyblockUtils.getWorldName() != "Lotus Atoll") return

        val frogsIterator = frogs.iterator()

        while (frogsIterator.hasNext()) {
            val (id, pending) = frogsIterator.next()

            if (!pending.entity.isAlive) {
                frogsIterator.remove()
                continue
            }
            if (pending.entity.scale == 4f && fishedPuddleJumper) {
                frogsIterator.remove()
                puddleJumpers[id] = TrackedPuddleJumper(pending)
                puddleJumpers[id]?.tickStages?.add(tickCounter)
                continue
            }

            if (pending.timestamp + TICK_WINDOW < tickCounter) {
                frogsIterator.remove()
            }
        }
        fishedPuddleJumper = false

        val puddleIterator = puddleJumpers.iterator()

        while (puddleIterator.hasNext()) {
            val (_, puddleJumper) = puddleIterator.next()

            if ((puddleJumper.jumpStage % 2 == 1) xor puddleJumper.frog.entity.jumpAnimationState.isStarted) {
                puddleJumper.jumpStage++
                puddleJumper.tickStages.add(tickCounter)
            }

            if (puddleJumper.jumpStage >= 8 && puddleJumper.deathTimer == 0) puddleJumper.deathTimer = predictDeathTick(puddleJumper.tickStages)

            if (puddleJumper.deathTimer != 0 && puddleJumper.deathTimer < tickCounter - 40) puddleJumper.deathTimer = tickCounter + 80 // Incorrectly thought puddle jumper dies in 4s

            if (QOL.puddleJumperAnnouncementToggle &&
                puddleJumper.deathTimer != 0 &&
                !puddleJumper.playedAnnouncement &&
                puddleJumper.deathTimer - tickCounter <= QOL.puddleJumperAnnouncementTimerTicks) {
                AnnouncementOverlay.queueAnnouncement(
                    Component.literal("${RED}Puddle Jumper$YELLOW arrives soon!"),
                    40,
                    SoundEvents.EXPERIENCE_ORB_PICKUP)
                puddleJumper.playedAnnouncement = true
            }
        }

        textOverlayData.lines = puddleJumpers.values.map {
            Component.literal("${RED}Puddle Jumper${WHITE}: $YELLOW" +
                    if (it.deathTimer == 0) {
                        "Jumping"
                    } else {
                        when(it.deathTimer - tickCounter) {
                            in 100..200 -> "Dying in ${YELLOW}${"%.2f".format((it.deathTimer - tickCounter) / 20f)}s"
                            in 40..100 -> "Dying in ${GOLD}${"%.2f".format((it.deathTimer - tickCounter) / 20f)}s"
                            in 0..40 -> "Dying in ${RED}${"%.2f".format((it.deathTimer - tickCounter) / 20f)}s"
                            else -> "Dying in ${RED}Soon"
                        }
                    } +
                    "${YELLOW}!")
        }
    }

    private fun onServerTick(event: ServerTickEvent) {
        if (!SkyblockUtils.isInSkyblock() || SkyblockUtils.getWorldName() != "Lotus Atoll") return

        tickCounter = event.tick
    }

    private fun onWorldChange(@Suppress("UNUSED_PARAMETER") event: WorldChangedEvent) {
        frogs.clear()
        puddleJumpers.clear()
    }

    private fun predictDeathTick(stages: List<Int>): Int {
        if (stages.size < 9) return tickCounter + 200

        val stage7 = stages[7]
        val stage8 = stages[8]

        val interval78 = stage8 - stage7

        val predictedDeath = stage8 + if (interval78 < 120) 80 else 200

        return predictedDeath
    }

}