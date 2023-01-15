package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedWatchableObject

class PacketPlayOutEntityMetadata(
    private val entityId: Int,
    private val objects: List<WrappedDataValue>
) : Packet() {
    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)

            packet.integers.write(0, this.entityId)
            packet.dataValueCollectionModifier.write(0, this.objects)

            return packet
        }
}