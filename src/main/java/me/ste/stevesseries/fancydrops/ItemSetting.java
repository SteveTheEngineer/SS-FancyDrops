package me.ste.stevesseries.fancydrops;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Pattern;

public class ItemSetting {
    public static Map<String, ItemSetting> ITEM_SETTINGS = new LinkedHashMap<>();
    private ItemSettingCondition condition;
    private ItemSettingValues values;

    public ItemSetting(ItemSettingCondition condition, ItemSettingValues values) {
        this.condition = condition;
        this.values = values;
    }

    public static ItemSetting read(ConfigurationSection sect) {
        List<Pattern> materials = new ArrayList<>();
        List<Pattern> displayNames = new ArrayList<>();
        List<NumberRange> amounts = new ArrayList<>();
        List<Pattern> worlds = new ArrayList<>();
        sect.getStringList("condition.materials").forEach((s) -> materials.add(Pattern.compile(s)));
        sect.getStringList("condition.displayNames").forEach((s) -> displayNames.add(Pattern.compile(s)));
        sect.getStringList("condition.amounts").forEach((s) -> amounts.add(NumberRange.parse(s)));
        sect.getStringList("condition.worlds").forEach((s) -> worlds.add(Pattern.compile(s)));
        ItemSettingCondition cond = new ItemSettingCondition(materials, displayNames, amounts, worlds);
        double eulerAngleX = sect.getDouble("values.eulerAngle.x");
        double eulerAngleY = sect.getDouble("values.eulerAngle.y");
        double eulerAngleZ = sect.getDouble("values.eulerAngle.z");
        boolean small = sect.getBoolean("values.small");
        boolean fancyItem = sect.getBoolean("values.fancyItem");
        boolean showHint = sect.getBoolean("values.showHint");
        boolean invisible = sect.getBoolean("values.invisible");
        boolean fixedRotation = sect.getBoolean("values.fixedRotation");
        String hint = sect.getString("values.hint");
        double itemOffsetX = sect.getDouble("values.itemOffset.x");
        double itemOffsetY = sect.getDouble("values.itemOffset.y");
        double itemOffsetZ = sect.getDouble("values.itemOffset.z");
        double hintOffsetX = sect.getDouble("values.hintOffset.x");
        double hintOffsetY = sect.getDouble("values.hintOffset.y");
        double hintOffsetZ = sect.getDouble("values.hintOffset.z");
        ArmorStandSlot armorStandSlot = ArmorStandSlot.valueOf(sect.getString("values.slot"));
        ItemSettingValues val = new ItemSettingValues(eulerAngleX, eulerAngleY, eulerAngleZ, small, fancyItem, showHint, invisible, fixedRotation, hint, itemOffsetX, itemOffsetY, itemOffsetZ, hintOffsetX, hintOffsetY, hintOffsetZ, armorStandSlot);
        return new ItemSetting(cond, val);
    }

    public static void reloadSettings() {
        ITEM_SETTINGS.clear();
        FileConfiguration conf = FancyDrops.getInstance().getConfig();
        ConfigurationSection ci = conf.getConfigurationSection("items");
        Objects.requireNonNull(ci).getKeys(false).forEach((s) -> {
            ConfigurationSection cur = ci.getConfigurationSection(s);
            ITEM_SETTINGS.put(s, read(Objects.requireNonNull(cur)));
        });
    }

    public static String matchItemSetting(World w, ItemStack is) {
        for (String k : ItemSetting.ITEM_SETTINGS.keySet()) {
            ItemSetting v = ItemSetting.ITEM_SETTINGS.get(k);
            ItemSettingCondition condition = v.getCondition();
            if (condition.check(w) && condition.check(is)) {
                return k;
            }
        }
        return null;
    }

    public ItemSettingCondition getCondition() {
        return this.condition;
    }

    public ItemSettingValues getValues() {
        return this.values;
    }
}
