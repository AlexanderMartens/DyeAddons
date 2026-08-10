package anlg.dyeaddons.events.models

/**
 * Fired once every server-side game tick, triggered by ClientboundPingPacket packets.
 */
data class ServerTickEvent(val tick: Int)