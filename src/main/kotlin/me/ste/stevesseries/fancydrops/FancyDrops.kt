package me.ste.stevesseries.fancydrops

import com.comphenix.protocol.ProtocolLibrary
import me.ste.stevesseries.fancydrops.item.FancyItem
import me.ste.stevesseries.fancydrops.listener.ItemListener
import me.ste.stevesseries.fancydrops.listener.PacketListener
import me.ste.stevesseries.fancydrops.listener.PlayerListener
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.plugin.java.JavaPlugin

class FancyDrops : JavaPlugin() {
    override fun onEnable() {
        this.saveDefaultConfig()
        this.reloadPluginConfiguration()

        this.server.pluginManager.registerEvents(ItemListener, this)
        this.server.pluginManager.registerEvents(PlayerListener, this)

        this.server.scheduler.runTaskTimer(this, Runnable {
            FancyItem.ITEMS.entries.removeIf {
                return@removeIf !it.value.update()
            }
        }, 0L, 1L)

        this.getCommand("fancydropsreload")!!.setExecutor { sender, _, _, _ ->
            if(sender.hasPermission("stevesseries.fancydrops.reload")) {
                this.reloadPluginConfiguration()
                for((_, item) in FancyItem.ITEMS) {
                    val preset = FancyItemPreset.matchPreset(item.item.itemStack)
                    if(preset != null) {
                        item.preset = preset
                    }
                    item.refresh()
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.config-reloaded")))
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.no-permission")))
            }
            return@setExecutor true
        }

        for(listener in PacketListener(this).listeners) {
            ProtocolLibrary.getProtocolManager().addPacketListener(listener)
        }

        for(world in Bukkit.getWorlds()) {
            for(item in world.getEntitiesByClass(Item::class.java)) {
                val preset = FancyItemPreset.matchPreset(item.itemStack)
                if(preset != null) {
                    val fancyItem = FancyItem(preset, item)
                    for(player in Bukkit.getOnlinePlayers()) {
                        fancyItem.addObserver(player)
                    }
                    FancyItem.ITEMS[item.uniqueId] = fancyItem
                }
            }
        }
    }

    override fun onDisable() {
        this.server.scheduler.cancelTasks(this)

        for(player in Bukkit.getOnlinePlayers()) {
            for((_, item) in FancyItem.ITEMS) {
                item.removeObserver(player)
            }
        }
    }

    private fun reloadPluginConfiguration() {
        this.reloadConfig()

        val presets = this.config.getConfigurationSection("presets")
        FancyItemPreset.PRESETS.clear()
        if (presets != null) {
            for (key in presets.getKeys(false)) {
                FancyItemPreset.PRESETS += FancyItemPreset(presets.getConfigurationSection(key)!!)
            }
        }
    }
}