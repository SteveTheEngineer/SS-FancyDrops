package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.ste.stevesseries.fancydrops.item.FancyItemArmorStand

class PacketPlayOutDestroyEntities(private val entityIds: IntArray) : Packet() {
    constructor(stands: Collection<FancyItemArmorStand>) : this(IntArray(stands.size)) {
        var i = 0
        for (stand in stands) {
            this.entityIds[i++] = stand.entityId
        }
    }

    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)

            packet.integerArrays.write(0, this.entityIds)

            return packet
        }
}