package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.ste.stevesseries.fancydrops.item.FancyItemArmorStand;

import java.util.Collection;

public class PacketPlayOutDestroyEntities {
    private final int[] entityIds;

    public PacketPlayOutDestroyEntities(int[] entityIds) {
        this.entityIds = entityIds;
    }

    public PacketPlayOutDestroyEntities(Collection<FancyItemArmorStand> stands) {
        this.entityIds = new int[stands.size()];
        int i = 0;
        for(FancyItemArmorStand stand : stands) {
            this.entityIds[i++] = stand.getEntityId();
        }
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, this.entityIds);
        return packet;
    }
}