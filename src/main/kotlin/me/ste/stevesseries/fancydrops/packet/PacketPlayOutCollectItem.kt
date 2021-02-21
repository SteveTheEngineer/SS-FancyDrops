package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer

class PacketPlayOutCollectItem(
    private val collectedId: Int,
    private val collectorId: Int,
    private val pickupItemCount: Int
) : Packet() {
    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT)

            packet.integers.write(0, this.collectedId)
            packet.integers.write(1, this.collectorId)
            packet.integers.write(2, this.pickupItemCount)

            return packet
        }
}
