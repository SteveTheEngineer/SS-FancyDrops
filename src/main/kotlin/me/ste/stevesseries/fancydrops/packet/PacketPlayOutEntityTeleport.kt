package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.Location

class PacketPlayOutEntityTeleport(
    private val entityId: Int,
    private val newLocation: Location,
    private val onGround: Boolean
) : Packet() {
    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT)

            packet.integers.write(0, this.entityId)
            packet.doubles.write(0, this.newLocation.x)
            packet.doubles.write(1, this.newLocation.y)
            packet.doubles.write(2, this.newLocation.z)
            packet.bytes.write(0, (this.newLocation.yaw * 256F / 360F).toInt().toByte())
            packet.bytes.write(1, (this.newLocation.pitch * 256F / 360F).toInt().toByte())
            packet.booleans.write(0, this.onGround)

            return packet
        }
}