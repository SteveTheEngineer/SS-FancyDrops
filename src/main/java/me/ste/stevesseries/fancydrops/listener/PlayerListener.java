package me.ste.stevesseries.fancydrops.listener;

import me.ste.stevesseries.fancydrops.item.FancyItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for(FancyItem item : FancyItem.ITEMS.values()) {
            if(item.getObservers().contains(event.getPlayer())) {
                item.removeObserver(event.getPlayer());
            }
        }
    }
}