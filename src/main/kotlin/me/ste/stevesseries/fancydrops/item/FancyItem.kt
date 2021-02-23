package me.ste.stevesseries.fancydrops.item

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.reflect.accessors.Accessors
import com.comphenix.protocol.utility.MinecraftReflection
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutDestroyEntities
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutEntityTeleport
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutSpawnEntity
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class FancyItem(var preset: FancyItemPreset, val item: Item) {
    companion object {
        private val NEXT_ENTITY_ID_ACCESSOR =
            Accessors.getMethodAccessor(MinecraftReflection.getEntityClass(), "nextEntityId")

        private fun nextEntityId() = this.NEXT_ENTITY_ID_ACCESSOR.invoke(null) as Int

        val ITEMS: MutableMap<UUID, FancyItem> = HashMap()

        fun getByEntityId(id: Int): FancyItem? {
            for ((_, item) in this.ITEMS) {
                if (item.item.entityId == id) {
                    return item
                }
            }
            return null
        }
    }

    val observers: MutableSet<Player> = HashSet()
    val entities: MutableSet<FancyItemArmorStand> = HashSet()
    var pickupEnabled = this.preset.rightClickPickup != FancyItemPreset.RightClickPickup.REQUIRED
    private var lastAmount = this.item.itemStack.amount

    init {
        this.createEntities()
    }

    private fun createEntities() {
        for ((_, preset) in this.preset.entities) {
            this.entities.add(FancyItemArmorStand(this, preset, nextEntityId(), UUID.randomUUID()))
        }
    }

    private fun broadcastPacket(packet: PacketContainer) {
        for (observer in this.observers) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(observer, packet)
        }
    }

    fun update(): Boolean {
        if (this.item.isDead) {
            for (observer in HashSet(this.observers)) {
                this.removeObserver(observer)
            }
            return false
        }

        if (this.lastAmount != this.item.itemStack.amount) {
            val preset = FancyItemPreset.matchPreset(this.item.itemStack)
            if (preset != null) {
                this.preset = preset
                this.pickupEnabled = preset.rightClickPickup != FancyItemPreset.RightClickPickup.REQUIRED
            } // Bringing back the vanilla item at this point is dangerous, and will lead to unknown behavior, so instead we just ignore the item amount change
            this.refresh()
            this.lastAmount = this.item.itemStack.amount
        }

        for (stand in this.entities) {
            val expected = stand.expectedLocation
            if (stand.location != expected) {
                this.broadcastPacket(
                    PacketPlayOutEntityTeleport(
                        stand.entityId,
                        expected,
                        this.item.isOnGround
                    ).container
                )
                stand.location = expected
            }
        }
        return true
    }

    fun removeObserver(observer: Player) {
        this.observers.remove(observer)

        ProtocolLibrary.getProtocolManager()
            .sendServerPacket(observer, PacketPlayOutDestroyEntities(this.entities).container)
    }

    fun addObserver(observer: Player) {
        this.observers.add(observer)

        for (stand in this.entities) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(observer, PacketPlayOutSpawnEntity(stand.entityId, stand.entityUuid, EntityType.ARMOR_STAND, stand.location, 0, Vector()).container)
            ProtocolLibrary.getProtocolManager().sendServerPacket(
                observer,
                PacketPlayOutEntityTeleport(stand.entityId, stand.location, this.item.isOnGround).container
            )
            ProtocolLibrary.getProtocolManager().sendServerPacket(observer, stand.getEntityMetadataPacket(observer))

            val equipmentPacket = stand.equipmentPacket
            if (equipmentPacket != null) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(observer, equipmentPacket)
            }
        }
    }

    fun refresh() {
        val observers: Set<Player> = HashSet(this.observers)
        for (observer in observers) {
            this.removeObserver(observer)
        }

        this.entities.clear()
        this.createEntities()

        for (observer in observers) {
            this.addObserver(observer)
        }
    }
}