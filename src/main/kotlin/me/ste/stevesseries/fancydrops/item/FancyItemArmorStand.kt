package me.ste.stevesseries.fancydrops.item

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityEquipment
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityMetadata
import me.ste.stevesseries.fancydrops.preset.ArmorStandPreset
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.experimental.or

class FancyItemArmorStand(
    private val fancyItem: FancyItem,
    private val preset: ArmorStandPreset,
    val entityId: Int,
    val entityUuid: UUID
) {
    var location = this.fancyItem.item.location

    init {
        if (this.preset.staticRotation) {
            this.location.yaw = 0F
            this.location.pitch = 0F
        }

        this.location = this.expectedLocation
    }

    val expectedLocation
        get() = this.fancyItem.item.location.add(
            this.preset.position.clone().rotateAroundY(Math.toRadians(-1 * (this.location.yaw + 90.0)))
        )

    val entityMetadataPacket: PacketContainer
        get() {
            val watcher = WrappedDataWatcher()

            var entityFlags: Byte = 0
            if (this.preset.invisible) {
                entityFlags = entityFlags or 0x20
            }
            watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte::class.javaObjectType), entityFlags)

            var customName = this.preset.customName
            if (customName != null) {
                val stack = this.fancyItem.item.itemStack

                customName = customName.replace("\$\$material\$\$", stack.type.name)
                customName = customName.replace("\$\$amount\$\$", stack.amount.toString())
                customName = customName.replace("\$\$displayName\$\$", stack.itemMeta?.displayName ?: "")

                watcher.setObject(
                    2,
                    WrappedDataWatcher.Registry.getChatComponentSerializer(true),
                    Optional.of(WrappedChatComponent.fromText(customName).handle)
                )
                watcher.setObject(
                    WrappedDataWatcher.WrappedDataWatcherObject(
                        3,
                        WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)
                    ), true
                ) // Yes, this one differs from the others, it provides WrappedDataWatcherObject instead of the index and the serializer, because it doesn't work the other way
            }

            var armorStandFlags: Byte = 0
            if (this.preset.marker) {
                armorStandFlags = armorStandFlags or 0x10
            }
            if (this.preset.small) {
                armorStandFlags = armorStandFlags or 0x01
            }
            if (this.preset.arms) {
                armorStandFlags = armorStandFlags or 0x04
            }
            if (this.preset.basePlate) {
                armorStandFlags = armorStandFlags or 0x08
            }
            watcher.setObject(14, WrappedDataWatcher.Registry.get(Byte::class.javaObjectType), armorStandFlags)

            val head = this.preset.slots[EquipmentSlot.HEAD]
            if (head != null) {
                watcher.setObject(15, WrappedDataWatcher.Registry.getVectorSerializer(), head.angle)
            }
            val body = this.preset.slots[EquipmentSlot.CHEST]
            if (body != null) {
                watcher.setObject(16, WrappedDataWatcher.Registry.getVectorSerializer(), body.angle)
            }
            val leftArm = this.preset.slots[EquipmentSlot.OFF_HAND]
            if (leftArm != null) {
                watcher.setObject(17, WrappedDataWatcher.Registry.getVectorSerializer(), leftArm.angle)
            }
            val rightArm = this.preset.slots[EquipmentSlot.HAND]
            if (rightArm != null) {
                watcher.setObject(18, WrappedDataWatcher.Registry.getVectorSerializer(), rightArm.angle)
            }
            val leftLeg = this.preset.slots[EquipmentSlot.LEGS]
            if (leftLeg != null) {
                watcher.setObject(19, WrappedDataWatcher.Registry.getVectorSerializer(), leftLeg.angle)
            }
            val rightLeg = this.preset.slots[EquipmentSlot.FEET]
            if (rightLeg != null) {
                watcher.setObject(20, WrappedDataWatcher.Registry.getVectorSerializer(), rightLeg.angle)
            }

            return PacketPlayOutEntityMetadata(this.entityId, watcher.watchableObjects).container
        }

    val equipmentPacket: PacketContainer?
        get() {
            val equipment: MutableList<Pair<EnumWrappers.ItemSlot, ItemStack>> = ArrayList()

            val head = this.preset.slots[EquipmentSlot.HEAD]
            if (head != null && (head.useItemItem || head.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.HEAD,
                    if (head.useItemItem) this.fancyItem.item.itemStack else ItemStack(head.item!!)
                )
            }
            val chest = this.preset.slots[EquipmentSlot.CHEST]
            if (chest != null && (chest.useItemItem || chest.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.CHEST,
                    if (chest.useItemItem) this.fancyItem.item.itemStack else ItemStack(chest.item!!)
                )
            }
            val offHand = this.preset.slots[EquipmentSlot.OFF_HAND]
            if (offHand != null && (offHand.useItemItem || offHand.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.OFFHAND,
                    if (offHand.useItemItem) this.fancyItem.item.itemStack else ItemStack(offHand.item!!)
                )
            }
            val mainHand = this.preset.slots[EquipmentSlot.HAND]
            if (mainHand != null && (mainHand.useItemItem || mainHand.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.MAINHAND,
                    if (mainHand.useItemItem) this.fancyItem.item.itemStack else ItemStack(mainHand.item!!)
                )
            }
            val legs = this.preset.slots[EquipmentSlot.LEGS]
            if (legs != null && (legs.useItemItem || legs.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.LEGS,
                    if (legs.useItemItem) this.fancyItem.item.itemStack else ItemStack(legs.item!!)
                )
            }
            val feet = this.preset.slots[EquipmentSlot.FEET]
            if (feet != null && (feet.useItemItem || feet.item != null)) {
                equipment += Pair(
                    EnumWrappers.ItemSlot.FEET,
                    if (feet.useItemItem) this.fancyItem.item.itemStack else ItemStack(feet.item!!)
                )
            }

            return if (equipment.isNotEmpty()) PacketPlayOutEntityEquipment(
                this.entityId,
                equipment
            ).container else null
        }
}