package anlg.dyeaddons.events

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.events.models.ServerBlockChangeEvent
import anlg.dyeaddons.events.models.PacketReceivedEvent
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket

object BlockPacketHandler {

    fun init() {
        EventBus.subscribe(PacketReceivedEvent::class, ::onPacket)
    }

    private fun onPacket(event: PacketReceivedEvent) {
        when (val packet = event.packet) {
            is ClientboundBlockUpdatePacket -> {
                val oldState = mc.level?.getBlockState(packet.pos) ?: return
                EventBus.publish(ServerBlockChangeEvent(packet.pos, oldState, packet.blockState))
            }

            is ClientboundSectionBlocksUpdatePacket -> {
                packet.runUpdates { pos, state ->
                    val oldState = mc.level?.getBlockState(pos) ?: return@runUpdates
                    EventBus.publish(ServerBlockChangeEvent(pos, oldState, state))
                }
            }
        }
    }
}