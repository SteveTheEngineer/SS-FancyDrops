package me.ste.stevesseries.fancydrops.item;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityEquipment;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityMetadata;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityTeleport;
import me.ste.stevesseries.fancydrops.preset.ArmorStandPreset;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FancyItemArmorStand {
    private final FancyItem fancyItem;
    private final ArmorStandPreset preset;
    private final int entityId;
    private final UUID entityUuid;
    private Location location;

    public FancyItemArmorStand(FancyItem fancyItem, ArmorStandPreset preset, int entityId, UUID entityUuid) {
        this.fancyItem = fancyItem;
        this.preset = preset;
        this.entityId = entityId;
        this.entityUuid = entityUuid;
        this.location = fancyItem.getItem().getLocation();

        if(this.preset.isStaticRotation()) {
            this.location.setYaw(0F);
            this.location.setPitch(0F);
        }

        this.location = this.getExpectedLocation();
    }

    public int getEntityId() {
        return this.entityId;
    }

    public UUID getEntityUuid() {
        return this.entityUuid;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getExpectedLocation() {
        return this.fancyItem.getItem().getLocation().add(this.preset.getPosition().clone().rotateAroundY(Math.toRadians(-1 * (this.location.getYaw() + 90))).toLocation(this.location.getWorld()));
    }

    public PacketContainer getTeleportPacket(Location newLocation) {
        return new PacketPlayOutEntityTeleport(this.entityId, newLocation, this.fancyItem.getItem().isOnGround()).toContainer();
    }

    public ArmorStandPreset getPreset() {
        return this.preset;
    }

    public PacketContainer getEntityMetadataPacket() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        byte entityFlags = 0;
        if(this.preset.isInvisible()) {
            entityFlags |= 0x20;
        }
        watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), entityFlags);

        String customName = this.preset.getCustomName();
        if(customName != null) {
            ItemStack stack = this.fancyItem.getItem().getItemStack();
            customName = customName.replaceAll("\\$\\$material\\$\\$", stack.getType().name()).replaceAll("\\$\\$amount\\$\\$", String.valueOf(stack.getAmount())).replaceAll("\\$\\$displayName\\$\\$", stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : "");
            watcher.setObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.of(WrappedChatComponent.fromText(customName).getHandle()));
            watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true); // Yes, this one differs from the others, it provides WrappedDataWatcherObject instead of the index and the serializer, because it doesn't work the other way
        }

        byte armorStandFlags = 0;
        if(this.preset.isMarker()) {
            armorStandFlags |= 0x10;
        }
        if(this.preset.isSmall()) {
            armorStandFlags |= 0x01;
        }
        if(this.preset.isArms()) {
            armorStandFlags |= 0x04;
        }
        if(!this.preset.isBasePlate()) {
            armorStandFlags |= 0x08;
        }
        watcher.setObject(14, WrappedDataWatcher.Registry.get(Byte.class), armorStandFlags);

        ArmorStandPreset.BodyPart head = this.preset.getSlots().get(EquipmentSlot.HEAD);
        if(head != null) {
            watcher.setObject(15, WrappedDataWatcher.Registry.getVectorSerializer(), head.getAngle());
        }
        ArmorStandPreset.BodyPart body = this.preset.getSlots().get(EquipmentSlot.CHEST);
        if(body != null) {
            watcher.setObject(16, WrappedDataWatcher.Registry.getVectorSerializer(), body.getAngle());
        }
        ArmorStandPreset.BodyPart leftArm = this.preset.getSlots().get(EquipmentSlot.OFF_HAND);
        if(leftArm != null) {
            watcher.setObject(17, WrappedDataWatcher.Registry.getVectorSerializer(), leftArm.getAngle());
        }
        ArmorStandPreset.BodyPart rightArm = this.preset.getSlots().get(EquipmentSlot.HAND);
        if(rightArm != null) {
            watcher.setObject(18, WrappedDataWatcher.Registry.getVectorSerializer(), rightArm.getAngle());
        }
        ArmorStandPreset.BodyPart leftLeg = this.preset.getSlots().get(EquipmentSlot.LEGS);
        if(leftLeg != null) {
            watcher.setObject(19, WrappedDataWatcher.Registry.getVectorSerializer(), leftLeg.getAngle());
        }
        ArmorStandPreset.BodyPart rightLeg = this.preset.getSlots().get(EquipmentSlot.FEET);
        if(rightLeg != null) {
            watcher.setObject(20, WrappedDataWatcher.Registry.getVectorSerializer(), rightLeg.getAngle());
        }

        return new PacketPlayOutEntityMetadata(this.entityId, watcher.getWatchableObjects()).toContainer();
    }

    public PacketContainer getEquipmentPacket() {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();

        ArmorStandPreset.BodyPart head = this.preset.getSlots().get(EquipmentSlot.HEAD);
        if(head != null && (head.useItemItem() || head.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, head.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(head.getItem())));
        }
        ArmorStandPreset.BodyPart chest = this.preset.getSlots().get(EquipmentSlot.CHEST);
        if(chest != null && (chest.useItemItem() || chest.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chest.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(chest.getItem())));
        }
        ArmorStandPreset.BodyPart offHand = this.preset.getSlots().get(EquipmentSlot.OFF_HAND);
        if(offHand != null && (offHand.useItemItem() || offHand.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, offHand.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(offHand.getItem())));
        }
        ArmorStandPreset.BodyPart mainHand = this.preset.getSlots().get(EquipmentSlot.HAND);
        if(mainHand != null && (mainHand.useItemItem() || mainHand.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, mainHand.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(mainHand.getItem())));
        }
        ArmorStandPreset.BodyPart legs = this.preset.getSlots().get(EquipmentSlot.LEGS);
        if(legs != null && (legs.useItemItem() || legs.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, legs.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(legs.getItem())));
        }
        ArmorStandPreset.BodyPart feet = this.preset.getSlots().get(EquipmentSlot.FEET);
        if(feet != null && (feet.useItemItem() || feet.getItem() != null)) {
            equipment.add(new Pair<>(EnumWrappers.ItemSlot.FEET, feet.useItemItem() ? this.fancyItem.getItem().getItemStack() : new ItemStack(feet.getItem())));
        }

        return equipment.size() > 0 ? new PacketPlayOutEntityEquipment(this.entityId, equipment).toContainer() : null;
    }
}