package me.ste.stevesseries.fancydrops;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.List;

public class ItemSettingValues {
    private double eulerAngleX;
    private double eulerAngleY;
    private double eulerAngleZ;
    private boolean small;
    private boolean fancyItem;
    private boolean showHint;
    private boolean invisible;
    private boolean fixedRotation;
    private String hint;
    private double itemOffsetX;
    private double itemOffsetY;
    private double itemOffsetZ;
    private double hintOffsetX;
    private double hintOffsetY;
    private double hintOffsetZ;
    private ArmorStandSlot armorStandSlot;

    public ItemSettingValues(double eulerAngleX, double eulerAngleY, double eulerAngleZ, boolean small, boolean fancyItem, boolean showHint, boolean invisible, boolean fixedRotation, String hint, double itemOffsetX, double itemOffsetY, double itemOffsetZ, double hintOffsetX, double hintOffsetY, double hintOffsetZ, ArmorStandSlot armorStandSlot) {
        this.eulerAngleX = eulerAngleX;
        this.eulerAngleY = eulerAngleY;
        this.eulerAngleZ = eulerAngleZ;
        this.small = small;
        this.fancyItem = fancyItem;
        this.showHint = showHint;
        this.invisible = invisible;
        this.fixedRotation = fixedRotation;
        this.hint = hint;
        this.itemOffsetX = itemOffsetX;
        this.itemOffsetY = itemOffsetY;
        this.itemOffsetZ = itemOffsetZ;
        this.hintOffsetX = hintOffsetX;
        this.hintOffsetY = hintOffsetY;
        this.hintOffsetZ = hintOffsetZ;
        this.armorStandSlot = armorStandSlot;
    }

    public double getItemOffsetX() {
        return itemOffsetX;
    }

    public double getItemOffsetY() {
        return itemOffsetY;
    }

    public double getItemOffsetZ() {
        return itemOffsetZ;
    }

    public double getHintOffsetX() {
        return hintOffsetX;
    }

    public double getHintOffsetY() {
        return hintOffsetY;
    }

    public double getHintOffsetZ() {
        return hintOffsetZ;
    }

    public double getEulerAngleX() {
        return eulerAngleX;
    }

    public double getEulerAngleY() {
        return eulerAngleY;
    }

    public double getEulerAngleZ() {
        return eulerAngleZ;
    }

    public EulerAngle getEulerAngle() {
        return new EulerAngle(eulerAngleX, eulerAngleY, eulerAngleZ);
    }

    public boolean isSmall() {
        return small;
    }

    public boolean isFancyItem() {
        return fancyItem;
    }

    public boolean isShowHint() {
        return showHint;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public String getHint() {
        return hint;
    }

    public String getProcessedHint(ItemStack is) {
        String rh = hint;
        rh = FancyDrops.replaceSmart(rh, "%%Material%%", is.getType().name());
        rh = FancyDrops.replaceSmart(rh, "%%Amount%%", is.getAmount() + "");
        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            rh = FancyDrops.replaceSmart(rh, "%%DisplayName%%", is.getItemMeta().getDisplayName());
        } else {
            rh = FancyDrops.replaceSmart(rh, "%%DisplayName%%", StringUtils.capitalize(is.getType().name().replace('_', ' ')));
        }

        rh = ChatColor.translateAlternateColorCodes('&', rh);
        return rh;
    }

    public ArmorStandSlot getArmorStandSlot() {
        return armorStandSlot;
    }

    public Location applyItemOffset(Location loc) {
        Location newLoc = loc.clone();
        newLoc = newLoc.add(itemOffsetX, itemOffsetY, itemOffsetZ);
        if (fixedRotation) {
            newLoc.setYaw(0);
            newLoc.setPitch(0);
        }
        return newLoc;
    }

    public Location applyHintOffset(Location loc) {
        Location newLoc = loc.clone();
        newLoc = newLoc.add(hintOffsetX, hintOffsetY, hintOffsetZ);
        newLoc.setYaw(0);
        newLoc.setPitch(0);
        return newLoc;
    }
}
