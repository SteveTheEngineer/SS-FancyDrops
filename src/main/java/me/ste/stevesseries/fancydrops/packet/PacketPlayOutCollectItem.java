package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

public class PacketPlayOutCollectItem {
    private final int collectedId;
    private final int collectorId;
    private final int pickupItemCount;

    public PacketPlayOutCollectItem(int collectedId, int collectorId, int pickupItemCount) {
        this.collectedId = collectedId;
        this.collectorId = collectorId;
        this.pickupItemCount = pickupItemCount;
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT);
        packet.getIntegers().write(0, this.collectedId);
        packet.getIntegers().write(1, this.collectorId);
        packet.getIntegers().write(2, this.pickupItemCount);
        return packet;
    }
}