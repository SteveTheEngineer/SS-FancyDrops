package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import net.minecraft.util.MathHelper
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.lang.Double.max
import java.lang.Double.min
import java.util.*

class PacketPlayOutSpawnEntity(
    private val entityId: Int,
    private val entityUuid: UUID,
    private val entityType: EntityType,
    private val location: Location,
    private val data: Int,
    private val velocity: Vector
) : Packet() {
    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY)

            packet.integers.write(0, this.entityId)
            packet.uuiDs.write(0, this.entityUuid)
            packet.entityTypeModifier.write(0, this.entityType)
            packet.doubles.write(0, this.location.x)
            packet.doubles.write(1, this.location.y)
            packet.doubles.write(2, this.location.z)
            packet.integers.write(1, (max(-3.9, min(3.9, this.velocity.x)) * 8000.0).toInt())
            packet.integers.write(2, (max(-3.9, min(3.9, this.velocity.y)) * 8000).toInt())
            packet.integers.write(3, (max(-3.9, min(3.9, this.velocity.z)) * 8000).toInt())
            packet.bytes.write(0, (this.location.pitch * 256F / 360F).toInt().toByte())
            packet.bytes.write(1, (this.location.yaw * 256F / 360F).toInt().toByte())
            packet.bytes.write(2, (this.location.yaw * 256F / 360F).toInt().toByte()) //headYaw
            packet.integers.write(4, this.data)

            return packet
        }
}