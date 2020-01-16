package me.ste.stevesseries.fancydrops;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ItemSettingCondition {
    private List<Pattern> materials;
    private List<Pattern> displayNames;
    private List<NumberRange> amounts;
    private List<Pattern> worlds;

    public ItemSettingCondition(List<Pattern> materials, List<Pattern> displayNames, List<NumberRange> amounts, List<Pattern> worlds) {
        this.materials = materials;
        this.displayNames = displayNames;
        this.amounts = amounts;
        this.worlds = worlds;
    }

    public List<Pattern> getMaterials() {
        return this.materials;
    }

    public List<Pattern> getDisplayNames() {
        return this.displayNames;
    }

    public List<NumberRange> getAmounts() {
        return this.amounts;
    }

    public List<Pattern> getWorlds() {
        return this.worlds;
    }

    public boolean check(ItemStack is) {
        if (is == null) {
            return false;
        }
        if (!check(is.getType())) {
            return false;
        }
        if (!check(is.getAmount())) {
            return false;
        }
        return !is.hasItemMeta() || !Objects.requireNonNull(is.getItemMeta()).hasDisplayName() || check(is.getItemMeta().getDisplayName());
    }

    public boolean check(World w) {
        if (w == null) {
            return false;
        }
        if (!worlds.isEmpty()) {
            for (Pattern world : worlds) {
                if (world.matcher(w.getName()).matches()) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean check(Material m) {
        if (m == null) {
            return false;
        }
        if (!materials.isEmpty()) {
            for (Pattern material : materials) {
                if (material.matcher(m.name()).matches()) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean check(String dn) {
        if (dn == null) {
            return false;
        }
        if (!displayNames.isEmpty()) {
            for (Pattern displayName : displayNames) {
                if (displayName.matcher(dn).matches()) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean check(int a) {
        if (!amounts.isEmpty()) {
            for (NumberRange amountr : amounts) {
                if (amountr.match(a)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
