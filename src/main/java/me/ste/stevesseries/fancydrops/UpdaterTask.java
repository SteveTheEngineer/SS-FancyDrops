package me.ste.stevesseries.fancydrops;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.HashSet;
import java.util.UUID;

public class UpdaterTask implements Runnable {
    private FancyDrops plugin = FancyDrops.getInstance();

    @Override
    public void run() {
        for (UUID k : new HashSet<>(plugin.ITEMS.keySet())) {
            UUID v = plugin.ITEMS.get(k);
            if (k == null) {
                continue;
            } else if (v == null) {
                plugin.ITEMS.remove(k);
                continue;
            }
            Item item = plugin.ITEM_INSTANCES.get(k);
            Entity fancyItem = plugin.FANCY_ITEM_INSTANCES.get(k);
            if (item.isDead()) {
                if (fancyItem != null) {
                    fancyItem.remove();
                }
                plugin.ITEMS.remove(k);
                continue;
            }
            if (fancyItem == null || fancyItem.isDead()) {
                plugin.ITEMS.remove(k);
                continue;
            }
            String settingName = ItemSetting.matchItemSetting(item.getWorld(), item.getItemStack());
            if (settingName != null) {
                ItemSetting setting = ItemSetting.ITEM_SETTINGS.get(settingName);

                fancyItem.teleport(setting.getValues().applyItemOffset(item.getLocation()));
                fancyItem.setVelocity(item.getVelocity());
            }
        }
        for (UUID k : new HashSet<>(plugin.HINTS.keySet())) {
            UUID v = plugin.HINTS.get(k);
            if (k == null) {
                continue;
            } else if (v == null) {
                plugin.HINTS.remove(k);
                continue;
            }
            Item item = plugin.ITEM_INSTANCES.get(k);
            Entity hint = plugin.HINT_INSTANCES.get(k);
            if (item.isDead()) {
                if (hint != null) {
                    hint.remove();
                }
                plugin.HINTS.remove(k);
                continue;
            }
            if (hint == null || hint.isDead()) {
                plugin.HINTS.remove(k);
                continue;
            }
            String settingName = ItemSetting.matchItemSetting(item.getWorld(), item.getItemStack());
            if (settingName != null) {
                ItemSetting setting = ItemSetting.ITEM_SETTINGS.get(settingName);

                hint.teleport(setting.getValues().applyHintOffset(item.getLocation()));
                hint.setVelocity(item.getVelocity());
                hint.setCustomName(setting.getValues().getProcessedHint(item.getItemStack()));
            }
        }
    }
}