package me.ste.stevesseries.fancydrops.item

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityEquipment
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityMetadata
import me.ste.stevesseries.fancydrops.preset.ArmorStandPreset
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import com.comphenix.protocol.wrappers.Pair
import me.ste.stevesseries.fancydrops.extensions.dataValues
import java.util.*
import kotlin.experimental.or

class FancyItemArmorStand(
    private val fancyItem: FancyItem,
    private val preset: ArmorStandPreset,
    val entityId: Int,
    val entityUuid: UUID
) {
    var currentCustomName = this.getCustomName()
    var location = this.fancyItem.item.location
    private val customNameObservers: MutableSet<UUID> = HashSet()

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

    val customNameBoundingBox: BoundingBox?
        get() {
            if (this.preset.customNameBoundingBox == null) {
                return null
            }
            val radians = Math.toRadians(-1 * (this.location.yaw + 90.0))
            return BoundingBox.of(
                this.preset.customNameBoundingBox!!.min.rotateAroundY(radians),
                this.preset.customNameBoundingBox!!.max.rotateAroundY(radians)
            ).shift(this.fancyItem.item.location)
        }

    fun setCustomNameObserverStatus(player: Player, status: Boolean) {
        if (this.customNameObservers.contains(player.uniqueId) != status) {
            if (status) {
                this.customNameObservers.add(player.uniqueId)
            } else {
                this.customNameObservers.remove(player.uniqueId)
            }

            if (this.preset.customName != null) {
                val watcher = WrappedDataWatcher()
                watcher.setObject(
                    WrappedDataWatcher.WrappedDataWatcherObject(
                        3,
                        WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)
                    ), this.customNameObservers.contains(player.uniqueId) || this.preset.customNameBoundingBox == null
                )
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, PacketPlayOutEntityMetadata(this.entityId, watcher.dataValues).container)
            }
        }
    }

    private fun getCustomName(): String? {
        var customName = this.preset.customName ?: return null

        val stack = this.fancyItem.item.itemStack

        customName = customName.replace("\$\$material\$\$", stack.type.name)
        customName = customName.replace("\$\$amount\$\$", stack.amount.toString())
        customName = customName.replace("\$\$displayName\$\$", stack.itemMeta?.displayName ?: "")
        customName = customName.replace("\$\$customName\$\$", this.fancyItem.item.customName ?: "")

        return customName
    }

    fun refreshCustomName() {
        val customName = this.getCustomName() ?: return

        if (this.currentCustomName == customName) {
            return
        }
        this.currentCustomName = customName

        val watcher = WrappedDataWatcher()

        watcher.setObject(
            2,
            WrappedDataWatcher.Registry.getChatComponentSerializer(true),
            Optional.of(WrappedChatComponent.fromText(customName).handle)
        )

        for (player in this.fancyItem.observers) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, PacketPlayOutEntityMetadata(this.entityId, watcher.dataValues).container)
        }
    }

    fun getEntityMetadataPacket(player: Player): PacketContainer {
        val watcher = WrappedDataWatcher()

        var entityFlags: Byte = 0
        if (this.preset.invisible) {
            entityFlags = entityFlags or 0x20
        }
        watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte::class.javaObjectType), entityFlags)

        if (this.currentCustomName != null) {
            watcher.setObject(
                2,
                WrappedDataWatcher.Registry.getChatComponentSerializer(true),
                Optional.of(WrappedChatComponent.fromText(this.currentCustomName).handle)
            )
            watcher.setObject(
                WrappedDataWatcher.WrappedDataWatcherObject(
                    3,
                    WrappedDataWatcher.Registry.get(Boolean::class.javaObjectType)
                ), this.customNameObservers.contains(player.uniqueId) || this.preset.customNameBoundingBox == null
            ) // This one differs from the others, it provides WrappedDataWatcherObject instead of the index and the serializer, because it doesn't work the other way
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
        watcher.setObject(15, WrappedDataWatcher.Registry.get(Byte::class.javaObjectType), armorStandFlags)

        val head = this.preset.slots[EquipmentSlot.HEAD]
        if (head != null) {
            watcher.setObject(16, WrappedDataWatcher.Registry.getVectorSerializer(), head.angle)
        }
        val body = this.preset.slots[EquipmentSlot.CHEST]
        if (body != null) {
            watcher.setObject(17, WrappedDataWatcher.Registry.getVectorSerializer(), body.angle)
        }
        val leftArm = this.preset.slots[EquipmentSlot.OFF_HAND]
        if (leftArm != null) {
            watcher.setObject(18, WrappedDataWatcher.Registry.getVectorSerializer(), leftArm.angle)
        }
        val rightArm = this.preset.slots[EquipmentSlot.HAND]
        if (rightArm != null) {
            watcher.setObject(19, WrappedDataWatcher.Registry.getVectorSerializer(), rightArm.angle)
        }
        val leftLeg = this.preset.slots[EquipmentSlot.LEGS]
        if (leftLeg != null) {
            watcher.setObject(20, WrappedDataWatcher.Registry.getVectorSerializer(), leftLeg.angle)
        }
        val rightLeg = this.preset.slots[EquipmentSlot.FEET]
        if (rightLeg != null) {
            watcher.setObject(21, WrappedDataWatcher.Registry.getVectorSerializer(), rightLeg.angle)
        }

        return PacketPlayOutEntityMetadata(this.entityId, watcher.dataValues).container
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