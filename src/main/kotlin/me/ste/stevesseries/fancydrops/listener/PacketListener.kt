package me.ste.stevesseries.fancydrops.listener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.comphenix.protocol.wrappers.EnumWrappers
import me.ste.stevesseries.fancydrops.item.FancyItem
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.plugin.Plugin

class PacketListener(plugin: Plugin) {
    val listeners: MutableSet<PacketListener> = HashSet()

    init {
        this.listeners += object : PacketAdapter(plugin, PacketType.Play.Server.SPAWN_ENTITY) {
            override fun onPacketSending(event: PacketEvent) {
                if(event.packet.entityTypeModifier.read(0) == EntityType.DROPPED_ITEM) {
                    val fancyItem = FancyItem.ITEMS[event.packet.uuiDs.read(0)]
                    if(fancyItem != null) {
                        event.isCancelled = true
                        fancyItem.addObserver(event.player)
                    }
                }
            }
        }

        this.listeners += object : PacketAdapter(plugin, PacketType.Play.Server.ENTITY_DESTROY) {
            override fun onPacketSending(event: PacketEvent) {
                val ids: MutableSet<Int> = HashSet()
                for(id in event.packet.intLists.read(0)) {
                    val fancyItem = FancyItem.getByEntityId(id)
                    if(fancyItem != null) {
                        fancyItem.removeObserver(event.player)
                    } else {
                        ids += id
                    }
                }

                event.packet.intLists.write(0, ids.toList())
                if(ids.isEmpty()) {
                    event.isCancelled = true
                }
            }
        }

        this.listeners += object : PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            override fun onPacketReceiving(event: PacketEvent) {
                val enumEntityUseAction = event.packet.enumEntityUseActions.read(0)
                val entityUseAction = enumEntityUseAction.action

                if(event.player.gameMode != GameMode.SPECTATOR && (entityUseAction == EnumWrappers.EntityUseAction.INTERACT || entityUseAction == EnumWrappers.EntityUseAction.INTERACT_AT)) {
                    val entityId = event.packet.integers.read(0)
                    for((_, item) in FancyItem.ITEMS) {
                        for(stand in item.entities) {
                            if(stand.entityId == entityId) {
                                if(item.preset.rightClickPickup != FancyItemPreset.RightClickPickup.DISABLED && item.item.pickupDelay <= 0) {
                                    this.plugin.server.scheduler.runTask(this.plugin, Runnable {
                                        item.item.teleport(event.player)
                                    })

                                    item.pickupEnabled = true
                                }
                                event.isCancelled = true
                                return
                            }
                        }
                    }
                }
            }
        }
    }
}