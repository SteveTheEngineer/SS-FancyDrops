package me.ste.stevesseries.fancydrops.listener;

import com.comphenix.protocol.ProtocolLibrary;
import me.ste.stevesseries.fancydrops.item.FancyItem;
import me.ste.stevesseries.fancydrops.item.FancyItemArmorStand;
import me.ste.stevesseries.fancydrops.packet.PacketPlayOutCollectItem;
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class ItemListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        FancyItemPreset preset = FancyItemPreset.matchPreset(event.getEntity().getItemStack());
        if(preset != null) {
            FancyItem.ITEMS.put(event.getEntity().getUniqueId(), new FancyItem(preset, event.getEntity()));
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        FancyItem fancyItem = FancyItem.ITEMS.get(event.getItem().getUniqueId());
        if(event.getEntity() instanceof Player && fancyItem != null) {
            if(fancyItem.isPickupEnabled()) {
                //event.getEntity().getWorld().playSound(event.getEntity().getLocation().add(0D, 0.5D, 0D), Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.7F + 1F) * 2F); // By the way, yes, this fully replicates the vanilla behavior
                for(FancyItemArmorStand stand : fancyItem.getEntities()) {
                    ProtocolLibrary.getProtocolManager().broadcastServerPacket(new PacketPlayOutCollectItem(stand.getEntityId(), event.getEntity().getEntityId(), fancyItem.getItem().getItemStack().getAmount()).toContainer());
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}