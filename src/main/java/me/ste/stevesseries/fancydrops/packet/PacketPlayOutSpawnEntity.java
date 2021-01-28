package me.ste.stevesseries.fancydrops.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.ste.stevesseries.fancydrops.item.FancyItemArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PacketPlayOutSpawnEntity {
    private final int entityId;
    private final UUID entityUuid;
    private final EntityType entityType;
    private final Location location;
    private final int data;
    private final Vector velocity;

    public PacketPlayOutSpawnEntity(int entityId, UUID entityUuid, EntityType entityType, Location location, int data, Vector velocity) {
        this.entityId = entityId;
        this.entityUuid = entityUuid;
        this.entityType = entityType;
        this.location = location;
        this.data = data;
        this.velocity = velocity;
    }

    public PacketPlayOutSpawnEntity(FancyItemArmorStand stand) {
        this(stand.getEntityId(), stand.getEntityUuid(), EntityType.ARMOR_STAND, stand.getLocation(), 0, new Vector(0, 0, 0));
    }

    public PacketContainer toContainer() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, this.entityId); // Entity ID
        packet.getUUIDs().write(0, this.entityUuid); // Entity UUID
        packet.getEntityTypeModifier().write(0, this.entityType); // Entity Type
        packet.getDoubles().write(0, this.location.getX()); // Position X
        packet.getDoubles().write(1, this.location.getY()); // Position Y
        packet.getDoubles().write(2, this.location.getZ()); // Position Z
        packet.getIntegers().write(4, (int) (this.location.getYaw() * 256.0F / 360.0F)); // Yaw
        packet.getIntegers().write(5, (int) (this.location.getPitch() * 256.0F / 360.0F)); // Pitch
        packet.getIntegers().write(6, this.data); // Data
        packet.getIntegers().write(1, (int) (this.velocity.getX() * 8000)); // Velocity X
        packet.getIntegers().write(2, (int) (this.velocity.getY() * 8000)); // Velocity Y
        packet.getIntegers().write(3, (int) (this.velocity.getZ() * 8000)); // Velocity Z
        return packet;
    }
}