package me.ste.stevesseries.fancydrops.preset;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FancyItemPreset {
    public enum RightClickPickup {
        DISABLED,
        ENABLED,
        REQUIRED
    }

    public static final List<FancyItemPreset> PRESETS = new LinkedList<>();

    private Map<String, ArmorStandPreset> entities = new HashMap<>();

    private final Set<Pattern> matchMaterials = new HashSet<>();
    private final Set<Pattern> matchDisplayNames = new HashSet<>();
    private int minAmount = 0;
    private int maxAmount = 127;

    private RightClickPickup rightClickPickup = RightClickPickup.DISABLED;

    public FancyItemPreset(ConfigurationSection section) {
        ConfigurationSection entities = section.getConfigurationSection("entities");
        if (entities != null) {
            for(String key : entities.getKeys(false)) {
                this.entities.put(key, new ArmorStandPreset(entities.getConfigurationSection(key)));
            }
        }
        ConfigurationSection match = section.getConfigurationSection("match");
        if (match != null) {
            if(match.isList("materials")) {
                for(String pattern : match.getStringList("materials")) {
                    try {
                        this.matchMaterials.add(Pattern.compile(pattern));
                    } catch(PatternSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(match.isList("displayNames")) {
                for(String pattern : match.getStringList("displayNames")) {
                    try {
                        this.matchDisplayNames.add(Pattern.compile(pattern));
                    } catch(PatternSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(match.isInt("minAmount")) {
                this.minAmount = match.getInt("minAmount");
            }
            if(match.isInt("maxAmount")) {
                this.maxAmount = match.getInt("maxAmount");
            }
        }
        ConfigurationSection settings = section.getConfigurationSection("settings");
        if(settings != null) {
            if(settings.isString("rightClickPickup")) {
                try {
                    this.rightClickPickup = RightClickPickup.valueOf(settings.getString("rightClickPickup").toUpperCase());
                } catch(IllegalArgumentException ignored) {}
            }
        }
    }

    public Map<String, ArmorStandPreset> getEntities() {
        return this.entities;
    }

    public Set<Pattern> getMatchMaterials() {
        return this.matchMaterials;
    }

    public Set<Pattern> getMatchDisplayNames() {
        return this.matchDisplayNames;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public RightClickPickup getRightClickPickup() {
        return this.rightClickPickup;
    }

    public static FancyItemPreset matchPreset(ItemStack stack) {
        for(FancyItemPreset preset : FancyItemPreset.PRESETS) {
            boolean materialMatches = preset.getMatchMaterials().size() <= 0;
            if(!materialMatches) {
                for (Pattern pattern : preset.getMatchMaterials()) {
                    if (pattern.matcher(stack.getType().name()).matches()) {
                        materialMatches = true;
                        break;
                    }
                }
            }
            if(!materialMatches) {
                continue;
            }

            boolean displayNameMatches = preset.getMatchDisplayNames().size() <= 0;
            if(!displayNameMatches) {
                ItemMeta meta = stack.getItemMeta();
                if(meta == null || !meta.hasDisplayName()) {
                    continue;
                }
                for (Pattern pattern : preset.getMatchDisplayNames()) {
                    if (pattern.matcher(meta.getDisplayName()).matches()) {
                        displayNameMatches = true;
                        break;
                    }
                }
            }
            if(!displayNameMatches) {
                continue;
            }

            if(stack.getAmount() < preset.getMinAmount() || stack.getAmount() > preset.getMaxAmount()) {
                continue;
            }

            return preset;
        }
        return null;
    }
}