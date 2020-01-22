package me.ste.stevesseries.fancydrops;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FancyDrops extends JavaPlugin {
    private static FancyDrops instance;
    public EntityHider entityHider;
    protected Map<UUID, UUID> ITEMS, HINTS;
    protected Map<UUID, Item> ITEM_INSTANCES;
    protected Map<UUID, ArmorStand> FANCY_ITEM_INSTANCES, HINT_INSTANCES;

    public FancyDrops() {
        instance = this;
    }

    public static FancyDrops getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        ITEMS = new HashMap<>();
        HINTS = new HashMap<>();
        ITEM_INSTANCES = new HashMap<>();
        FANCY_ITEM_INSTANCES = new HashMap<>();
        HINT_INSTANCES = new HashMap<>();
        File CONFIG_FILE = new File(getDataFolder(), "config.yml");
        if (!CONFIG_FILE.exists()) {
            saveDefaultConfig();
        }
        reloadSettings();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        getServer().getScheduler().runTaskTimer(this, new UpdaterTask(), 0L, 1L);
        getServer().getWorlds().forEach(w -> w.getEntitiesByClass(Item.class).forEach(this::replaceItem));
    }

    @Override
    public void onDisable() {
        new HashMap<>(ITEMS).forEach((k, v) -> {
            ArmorStand ve = FANCY_ITEM_INSTANCES.get(k);
            ve.remove();
            ITEM_INSTANCES.remove(k);
        });
        new HashMap<>(HINTS).forEach((k, v) -> {
            ArmorStand ve = HINT_INSTANCES.get(k);
            ve.remove();
            ITEM_INSTANCES.remove(k);
        });
        getServer().getScheduler().cancelTasks(this);
    }

    public void reloadSettings() {
        reloadConfig();
        ItemSetting.reloadSettings();
    }

    protected void removeItem(Item item) {
        if (!ITEMS.containsKey(item.getUniqueId()) && !HINTS.containsKey(item.getUniqueId())) {
            return;
        }
        if (ITEMS.containsKey(item.getUniqueId())) {
            UUID target = ITEMS.get(item.getUniqueId());
            if (target != null) {
                Entity targetEntity = FANCY_ITEM_INSTANCES.get(target);
                if (targetEntity != null) {
                    targetEntity.remove();
                    ITEMS.remove(item.getUniqueId());
                }
            }
        }
        if (HINTS.containsKey(item.getUniqueId())) {
            UUID target = HINTS.get(item.getUniqueId());
            if (target != null) {
                Entity targetEntity = HINT_INSTANCES.get(target);
                if (targetEntity != null) {
                    targetEntity.remove();
                    HINTS.remove(item.getUniqueId());
                }
            }
        }
        getServer().getOnlinePlayers().forEach(p -> entityHider.showEntity(p, item));
    }

    protected void replaceItem(Item item) {
        if (ITEMS.containsKey(item.getUniqueId()) && HINTS.containsKey(item.getUniqueId())) {
            return;
        }
        String settingName = ItemSetting.matchItemSetting(item.getWorld(), item.getItemStack());
        if (settingName == null) {
            return;
        }
        ItemSetting setting = ItemSetting.ITEM_SETTINGS.get(settingName);
        ItemSettingCondition condition = setting.getCondition();
        ItemSettingValues values = setting.getValues();
        if (values.isFancyItem()) {
            getServer().getOnlinePlayers().forEach(p -> entityHider.hideEntity(p, item));
            if (!ITEMS.containsKey(item.getUniqueId())) {
                ArmorStand fancyItem = (ArmorStand) item.getWorld().spawnEntity(values.applyItemOffset(item.getLocation()), EntityType.ARMOR_STAND);

                fancyItem.setVisible(false);
                fancyItem.setRemoveWhenFarAway(false);
                fancyItem.setSilent(true);
                fancyItem.setAI(false);
                fancyItem.setInvulnerable(true);
                fancyItem.setGravity(false);
                fancyItem.setBasePlate(false);
                fancyItem.setArms(true);
                
                if(values.isInvisible()) {
                    fancyItem.setMarker(true);
                }
                
                fancyItem.setSmall(values.isSmall());
                if (!values.isInvisible() && fancyItem.getEquipment() != null) {
                    if (values.getArmorStandSlot() == ArmorStandSlot.HEAD) {
                        fancyItem.setHeadPose(values.getEulerAngle());
                        fancyItem.getEquipment().setHelmet(item.getItemStack());
                    } else if (values.getArmorStandSlot() == ArmorStandSlot.CHEST) {
                        fancyItem.setBodyPose(values.getEulerAngle());
                        fancyItem.setLeftArmPose(values.getEulerAngle());
                        fancyItem.setRightArmPose(values.getEulerAngle());
                        fancyItem.getEquipment().setChestplate(item.getItemStack());
                    } else if (values.getArmorStandSlot() == ArmorStandSlot.LEGS) {
                        fancyItem.setLeftLegPose(values.getEulerAngle());
                        fancyItem.setRightLegPose(values.getEulerAngle());
                        fancyItem.getEquipment().setLeggings(item.getItemStack());
                    } else if (values.getArmorStandSlot() == ArmorStandSlot.FEET) {
                        fancyItem.setLeftLegPose(values.getEulerAngle());
                        fancyItem.setRightLegPose(values.getEulerAngle());
                        fancyItem.getEquipment().setBoots(item.getItemStack());
                    } else if (values.getArmorStandSlot() == ArmorStandSlot.HAND_LEFT) {
                        fancyItem.setLeftArmPose(values.getEulerAngle());
                        fancyItem.getEquipment().setItemInOffHand(item.getItemStack());
                    } else if (values.getArmorStandSlot() == ArmorStandSlot.HAND_RIGHT) {
                        fancyItem.setRightArmPose(values.getEulerAngle());
                        fancyItem.getEquipment().setItemInMainHand(item.getItemStack());
                    }
                }

                fancyItem.setVelocity(item.getVelocity());

                ITEMS.put(item.getUniqueId(), fancyItem.getUniqueId());
                FANCY_ITEM_INSTANCES.put(item.getUniqueId(), fancyItem);
            }
        }
        if (values.isShowHint()) {
            if (!HINTS.containsKey(item.getUniqueId())) {
                ArmorStand hint = (ArmorStand) item.getWorld().spawnEntity(values.applyHintOffset(item.getLocation()), EntityType.ARMOR_STAND);

                hint.setVisible(false);
                hint.setRemoveWhenFarAway(false);
                hint.setSilent(true);
                hint.setAI(false);
                hint.setInvulnerable(true);
                hint.setGravity(false);
                hint.setBasePlate(false);
                hint.setSmall(true);
                hint.setMarker(true);
                hint.setCustomNameVisible(true);

                hint.setCustomName(values.getProcessedHint(item.getItemStack()));

                hint.setVelocity(item.getVelocity());

                HINTS.put(item.getUniqueId(), hint.getUniqueId());
                HINT_INSTANCES.put(item.getUniqueId(), hint);
            }
        }
        if (values.isFancyItem() || values.isShowHint()) {
            ITEM_INSTANCES.put(item.getUniqueId(), item);
        }
    }

    public static String replaceSmart(String text, String from, String to) {
        String r;
        if (!from.equals(from.toLowerCase()) && !from.equals(from.toUpperCase())) {
            r = text.replaceAll(from, to);
            r = r.replaceAll(from.toLowerCase(), to.toLowerCase());
        } else {
            r = text.replaceAll(from.toLowerCase(), to.toLowerCase());
        }
        r = r.replaceAll(from.toUpperCase(), to.toUpperCase());
        return r;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("fancydropsreload")) {
            if (sender.hasPermission("stevesseries.fancydrops.reload")) {
                reloadSettings();
                String message = getConfig().getString("messages.pluginReloaded");
                message = replaceSmart(message, "%%Sender%%", sender.getName());
                message = ChatColor.translateAlternateColorCodes('&', message);
                sender.sendMessage(message);
            } else {
                String message = getConfig().getString("messages.noPermission");
                message = replaceSmart(message, "%%Sender%%", sender.getName());
                message = ChatColor.translateAlternateColorCodes('&', message);
                sender.sendMessage(message);
            }
            return true;
        }
        return false;
    }
}