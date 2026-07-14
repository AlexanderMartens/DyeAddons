package anlg.dyeaddons.events.models

import net.minecraft.network.protocol.Packet

data class PacketReceivedEvent(
    val packet: Packet<*>
)