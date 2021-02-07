package me.ste.stevesseries.fancydrops;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.ste.stevesseries.fancydrops.item.FancyItem;
import me.ste.stevesseries.fancydrops.item.FancyItemArmorStand;
import me.ste.stevesseries.fancydrops.listener.ItemListener;
import me.ste.stevesseries.fancydrops.listener.PlayerListener;
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class FancyDrops extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadPluginConfiguration();

        this.getServer().getPluginManager().registerEvents(new ItemListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        this.getServer().getScheduler().runTaskTimer(this, () -> FancyItem.ITEMS.entrySet().removeIf(item -> !item.getValue().update()), 0L, 1L);

        this.getCommand("fancydropsreload").setExecutor((sender, command, label, args) -> {
            if(sender.hasPermission("stevesseries.fancydrops.reload")) {
                this.reloadPluginConfiguration();
                for(FancyItem item : FancyItem.ITEMS.values()) {
                    FancyItemPreset preset = FancyItemPreset.matchPreset(item.getItem().getItemStack());
                    if(preset != null) {
                        item.setPreset(preset);
                    }
                    item.refresh();
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.config-reloaded")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages.no-permission")));
            }
            return true;
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPacket().getEntityTypeModifier().read(0) == EntityType.DROPPED_ITEM) {
                    UUID uniqueId = event.getPacket().getUUIDs().read(0);
                    FancyItem fancyItem = FancyItem.ITEMS.get(uniqueId);
                    if(fancyItem != null) {
                        event.setCancelled(true);
                        fancyItem.addObserver(event.getPlayer());
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_DESTROY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                for(int id : event.getPacket().getIntegerArrays().read(0)) {
                    FancyItem fancyItem = FancyItem.getByEntityId(id);
                    if(fancyItem != null) {
                        fancyItem.removeObserver(event.getPlayer());
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);
                if(event.getPlayer().getGameMode() != GameMode.SPECTATOR && (entityUseAction == EnumWrappers.EntityUseAction.INTERACT || entityUseAction  == EnumWrappers.EntityUseAction.INTERACT_AT) && event.getPacket().getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                    int entityId = event.getPacket().getIntegers().read(0);
                    for(FancyItem item : FancyItem.ITEMS.values()) {
                        for(FancyItemArmorStand stand : item.getEntities()) {
                            if(stand.getEntityId() == entityId) {
                                if(item.getPreset().getRightClickPickup() != FancyItemPreset.RightClickPickup.DISABLED && item.getItem().getPickupDelay() <= 0) {
                                    item.getItem().teleport(event.getPlayer());
                                    item.setPickupEnabled(true);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        });

        for(World world : Bukkit.getWorlds()) {
            for(Item item : world.getEntitiesByClass(Item.class)) {
                FancyItemPreset preset = FancyItemPreset.matchPreset(item.getItemStack());
                if(preset != null) {
                    FancyItem fancyItem = new FancyItem(preset, item);
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        fancyItem.addObserver(player);
                    }
                    FancyItem.ITEMS.put(item.getUniqueId(), fancyItem);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            for(FancyItem item : FancyItem.ITEMS.values()) {
                item.removeObserver(player);
            }
        }
    }

    private void reloadPluginConfiguration() {
        this.reloadConfig();

        ConfigurationSection presets = this.getConfig().getConfigurationSection("presets");
        FancyItemPreset.PRESETS.clear();
        if(presets != null) {
            for(String key : presets.getKeys(false)) {
                FancyItemPreset.PRESETS.add(new FancyItemPreset(presets.getConfigurationSection(key)));
            }
        }
    }
}