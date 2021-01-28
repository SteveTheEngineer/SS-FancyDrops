package me.ste.stevesseries.fancydrops.item;

import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutDestroyEntities;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityTeleport;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutSpawnEntity;
import me.ste.stevesseries.fancydrops.preset.ArmorStandPreset;
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FancyItem {
    private static final MethodAccessor NEXT_ENTITY_ID_ACCESSOR = Accessors.getMethodAccessor(MinecraftReflection.getEntityClass(), "nextEntityId");
    private static int nextEntityId() {
        return (int) FancyItem.NEXT_ENTITY_ID_ACCESSOR.invoke(null);
    }

    public static final Map<UUID, FancyItem> ITEMS = new HashMap<>();
    public static FancyItem getByEntityId(int id) {
        for(FancyItem item : FancyItem.ITEMS.values()) {
            if(item.getItem().getEntityId() == id) {
                return item;
            }
        }
        return null;
    }

    private FancyItemPreset preset;
    private final Item item;
    private final Set<Player> observers = new HashSet<>();
    private final Set<FancyItemArmorStand> entities = new HashSet<>();
    private boolean pickupEnabled;
    private int lastAmount;

    public FancyItem(FancyItemPreset preset, Item item) {
        this.preset = preset;
        this.item = item;
        this.lastAmount = item.getItemStack().getAmount();

        this.pickupEnabled = preset.getRightClickPickup() != FancyItemPreset.RightClickPickup.REQUIRED;

        this.createEntities();
    }

    private void createEntities() {
        for(ArmorStandPreset preset : this.preset.getEntities().values()) {
            this.entities.add(new FancyItemArmorStand(this, preset, FancyItem.nextEntityId(), UUID.randomUUID()));
        }
    }

    private void broadcastPacket(PacketContainer packet) {
        for(Player observer : this.observers) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean update() {
        if(this.item.isDead()) {
            for(Player observer : new HashSet<>(this.observers)) {
                this.removeObserver(observer);
            }
            return false;
        }

        if(this.lastAmount != this.item.getItemStack().getAmount()) {
            FancyItemPreset preset = FancyItemPreset.matchPreset(this.item.getItemStack());
            if(preset != null) {
                this.preset = preset;
                this.pickupEnabled = preset.getRightClickPickup() != FancyItemPreset.RightClickPickup.REQUIRED;
            } // Bringing back the vanilla item at this point is dangerous, and will lead to unknown behavior, so instead we just ignore the item amount change
            this.refresh();
            this.lastAmount = this.item.getItemStack().getAmount();
        }

        for(FancyItemArmorStand stand : this.entities) {
            Location expected = stand.getExpectedLocation();
            if(!stand.getLocation().equals(expected)) {
                this.broadcastPacket(stand.getTeleportPacket(expected));
                stand.setLocation(expected);
            }
        }
        return true;
    }

    public void setPreset(FancyItemPreset preset) {
        this.preset = preset;
    }

    public void removeObserver(Player observer) {
        this.observers.remove(observer);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(observer, new PacketPlayOutDestroyEntities(this.entities).toContainer());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void addObserver(Player observer) {
        this.observers.add(observer);

        for(FancyItemArmorStand stand : this.entities) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, new PacketPlayOutSpawnEntity(stand).toContainer());
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, new PacketPlayOutEntityTeleport(stand.getEntityId(), stand.getLocation(), this.item.isOnGround()).toContainer());
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, stand.getEntityMetadataPacket());

                PacketContainer equipmentPacket = stand.getEquipmentPacket();
                if(equipmentPacket != null) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(observer, equipmentPacket);
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        Set<Player> observers = new HashSet<>(this.observers);
        for(Player observer : observers) {
            this.removeObserver(observer);
        }

        this.entities.clear();
        this.createEntities();

        for(Player observer : observers) {
            this.addObserver(observer);
        }
    }

    public Item getItem() {
        return this.item;
    }

    public Set<Player> getObservers() {
        return this.observers;
    }

    public boolean isPickupEnabled() {
        return this.pickupEnabled;
    }

    public Set<FancyItemArmorStand> getEntities() {
        return this.entities;
    }

    public FancyItemPreset getPreset() {
        return this.preset;
    }

    public void setPickupEnabled(boolean pickupEnabled) {
        this.pickupEnabled = pickupEnabled;
    }
}