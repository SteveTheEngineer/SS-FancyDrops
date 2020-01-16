package me.ste.stevesseries.fancydrops;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.Arrays;

public class EventListener implements Listener {
    private final FancyDrops plugin = FancyDrops.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent e) {
        Arrays.asList(e.getChunk().getEntities()).forEach((ent) -> {
            if (ent instanceof Item) {
                plugin.replaceItem((Item) ent);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        Arrays.asList(e.getChunk().getEntities()).forEach((ent) -> {
            if (ent instanceof Item) {
                plugin.removeItem((Item) ent);
            }

        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent e) {
        plugin.replaceItem(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDespawn(ItemDespawnEvent e) {
        plugin.removeItem(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryItemPickup(InventoryPickupItemEvent e) {
        Item item = e.getItem();
        ItemSetting set = ItemSetting.ITEM_SETTINGS.get(ItemSetting.matchItemSetting(item.getWorld(), item.getItemStack()));
        if (set != null) {
            plugin.removeItem(e.getItem());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityItemPickup(EntityPickupItemEvent e) {
        Item item = e.getItem();
        ItemSetting set = ItemSetting.ITEM_SETTINGS.get(ItemSetting.matchItemSetting(item.getWorld(), item.getItemStack()));
        if (set != null) {
            if (e.getRemaining() <= 0) {
                plugin.removeItem(e.getItem());
                e.getItem().getWorld().playSound(e.getItem().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.4F);
            }
        }
    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMerge(ItemMergeEvent e) { FIXME: 1/15/2020 Currently doesn't work properly. Needs further investigation
        plugin.removeItem(e.getEntity());
        plugin.updateItem(e.getTarget());
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCombust(EntityCombustEvent e) {
        if (e.getEntity().getType() == EntityType.DROPPED_ITEM) {
            plugin.removeItem((Item) e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.ITEMS.keySet().forEach((u) -> plugin.entityHider.hideEntity(e.getPlayer(), Bukkit.getEntity(u)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (plugin.ITEMS.containsValue(e.getRightClicked().getUniqueId())) {
            e.setCancelled(true);
        }
    }
}