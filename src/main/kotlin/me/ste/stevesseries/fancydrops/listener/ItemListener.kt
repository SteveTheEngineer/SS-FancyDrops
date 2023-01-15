package me.ste.stevesseries.fancydrops.listener

import com.comphenix.protocol.ProtocolLibrary
import me.ste.stevesseries.fancydrops.item.FancyItem
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutCollectItem
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemSpawnEvent

object ItemListener : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onItemSpawn(event: ItemSpawnEvent) {
        val preset = FancyItemPreset.matchPreset(event.entity.itemStack)
        if (preset != null) {
            FancyItem.ITEMS[event.entity.uniqueId] = FancyItem(preset, event.entity)
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onEntityPickupItemVisual(event: EntityPickupItemEvent) {
        val fancyItem = FancyItem.ITEMS[event.item.uniqueId]
        if (fancyItem != null) {
            val stand = fancyItem.entities.firstOrNull() ?: return

            ProtocolLibrary.getProtocolManager().broadcastServerPacket(
                PacketPlayOutCollectItem(
                    stand.entityId,
                    event.entity.entityId,
                    fancyItem.item.itemStack.amount
                ).container
            )
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onEntityEntityPickupItemMechanics(event: EntityPickupItemEvent) {
        val fancyItem = FancyItem.ITEMS[event.item.uniqueId]
        if (fancyItem != null && !fancyItem.pickupEnabled) {
            event.isCancelled = true
        }
    }
}