package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;

public class PacketPlayOutEntityTeleport {
    private final int entityId;
    private final Location newLocation;
    private final boolean onGround;

    public PacketPlayOutEntityTeleport(int entityId, Location newLocation, boolean onGround) {
        this.entityId = entityId;
        this.newLocation = newLocation;
        this.onGround = onGround;
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, this.entityId);
        packet.getDoubles().write(0, this.newLocation.getX());
        packet.getDoubles().write(1, this.newLocation.getY());
        packet.getDoubles().write(2, this.newLocation.getZ());
        packet.getBytes().write(0, (byte) (this.newLocation.getYaw() * 256.0F / 360.0F));
        packet.getBytes().write(1, (byte) (this.newLocation.getPitch() * 256.0F / 360.0F));
        packet.getBooleans().write(0, this.onGround);
        return packet;
    }
}