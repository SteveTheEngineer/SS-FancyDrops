package me.ste.stevesseries.fancydrops.command

import me.ste.stevesseries.fancydrops.FancyDrops
import me.ste.stevesseries.fancydrops.item.FancyItem
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ReloadCommand(private val plugin: FancyDrops) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender.hasPermission("stevesseries.fancydrops.reload")) {
            this.plugin.reloadPluginConfiguration()

            for((_, item) in FancyItem.ITEMS) {
                val preset = FancyItemPreset.matchPreset(item.item.itemStack)
                if(preset != null) {
                    item.preset = preset
                }
                item.refresh()
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.messagesConfiguration.getString("config-reloaded")))
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.messagesConfiguration.getString("no-permission")))
        }
        return true
    }
}