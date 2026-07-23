package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.models.ServerBlockChangeEvent
import anlg.dyeaddons.events.models.ClientTickEvent
import anlg.dyeaddons.events.models.MinedBlock
import anlg.dyeaddons.events.models.OreMinedEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.utils.SkyblockUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

object MiningEventHandler {

    private val allowedSoundNames = setOf(
        "block.glass.break",
        "block.stone.break",
        "block.gravel.break",
        "block.wool.break",
        "entity.experience_orb.pickup",
        "block.metal.place",
    )

    private val minedBlocks = mutableMapOf<BlockPos, BlockState>()
    private var pendingBlocks = ArrayDeque<Pair<BlockPos, BlockState>>()

    private const val COLLECTION_TICKS = 4

    private var currentTick = 0

    private var collecting = false
    private var collectUntilTick = 0

    fun init() {
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
        EventBus.subscribe(ServerBlockChangeEvent::class, ::onBlockChange)
        EventBus.subscribe(ClientTickEvent::class, ::onTick)
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.isInSkyblock()) return
        val soundName = event.name
        if (soundName !in allowedSoundNames) return

        collecting = true

        collectUntilTick = maxOf(collectUntilTick, currentTick + COLLECTION_TICKS)

    }

    private fun onBlockChange(event: ServerBlockChangeEvent) {
        if (!SkyblockUtils.isInSkyblock()) return
        val oldState = event.oldState
        val newState = event.newState
        val oldBlock = oldState.block
        val newBlock = newState.block

        if (!collecting && mc.gameMode?.isDestroying != true) return

        if (oldState == newState) return
        if (oldBlock == Blocks.AIR || oldBlock == Blocks.BEDROCK) return
        if (newBlock != Blocks.AIR && newBlock != Blocks.BEDROCK) return

        val pos = event.pos
        val playerDistance = Minecraft.getInstance().player?.position()?.distanceTo(pos.center) ?: return
        if (playerDistance > 8.0) return

        pendingBlocks += event.pos to event.oldState
    }

    private fun onTick(@Suppress("UNUSED_PARAMETER") event: ClientTickEvent) {
        if (!SkyblockUtils.isInSkyblock()) return

        val processing = pendingBlocks
        pendingBlocks = ArrayDeque()

        while (processing.isNotEmpty()) {
            val (pos, state) = processing.removeFirst()
            minedBlocks[pos] = state
        }

        currentTick++

        if (!collecting || currentTick < collectUntilTick) return

        val finished = minedBlocks.toMap()

        minedBlocks.clear()
        collecting = false

        if (finished.isNotEmpty()) {
            EventBus.publish(
                OreMinedEvent(
                    finished.map { MinedBlock(it.key, it.value) }
                )
            )
        }
    }
}