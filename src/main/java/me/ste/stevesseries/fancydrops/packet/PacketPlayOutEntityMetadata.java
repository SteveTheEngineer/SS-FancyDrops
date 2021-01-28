package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PacketPlayOutEntityMetadata {
    private final int entityId;
    private final List<WrappedWatchableObject> objects;

    public PacketPlayOutEntityMetadata(int entityId, List<WrappedWatchableObject> objects) {
        this.entityId = entityId;
        this.objects = objects;
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, this.entityId);
        packet.getWatchableCollectionModifier().write(0, this.objects);
        return packet;
    }
}