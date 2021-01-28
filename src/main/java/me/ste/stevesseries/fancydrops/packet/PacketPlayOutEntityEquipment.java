package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PacketPlayOutEntityEquipment {
    private final int entityId;
    private final List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment;

    public PacketPlayOutEntityEquipment(int entityId, List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment) {
        this.entityId = entityId;
        this.equipment = equipment;
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, this.entityId);
        packet.getSlotStackPairLists().write(0, this.equipment);
        return packet;
    }
}