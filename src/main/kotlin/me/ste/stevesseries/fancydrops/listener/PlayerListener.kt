package me.ste.stevesseries.fancydrops.listener

import me.ste.stevesseries.fancydrops.item.FancyItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerListener : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        for ((_, item) in FancyItem.ITEMS) {
            if (event.player in item.observers) {
                item.removeObserver(event.player)
            }
        }
    }
}