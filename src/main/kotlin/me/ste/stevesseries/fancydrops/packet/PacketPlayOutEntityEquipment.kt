package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.inventory.ItemStack

class PacketPlayOutEntityEquipment(
    private val entityId: Int,
    private val equipment: List<Pair<EnumWrappers.ItemSlot, ItemStack>>
) : Packet() {
    override val container: PacketContainer
        get() {
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT)

            packet.integers.write(0, this.entityId)
            packet.slotStackPairLists.write(0, this.equipment)

            return packet
        }
}