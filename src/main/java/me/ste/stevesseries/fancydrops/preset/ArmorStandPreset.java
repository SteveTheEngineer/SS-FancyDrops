package me.ste.stevesseries.fancydrops.preset;

import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.Map;

public class ArmorStandPreset {
    public static class BodyPart {
        private Vector3F angle = new Vector3F(0F, 0F, 0F);
        private boolean useItemItem = false;
        private Material item = null;

        public BodyPart(ConfigurationSection section) {
            ConfigurationSection angle = section.getConfigurationSection("angle");
            if(angle != null) {
                if(angle.contains("x")) {
                    this.angle.setX((float) angle.getDouble("x"));
                }
                if(angle.contains("y")) {
                    this.angle.setY((float) angle.getDouble("y"));
                }
                if(angle.contains("z")) {
                    this.angle.setZ((float) angle.getDouble("z"));
                }
            }
            if(section.isString("item")) {
                String value = section.getString("item");
                if(value.equalsIgnoreCase("$item")) {
                    this.useItemItem = true;
                } else {
                    this.item = Material.getMaterial(value);
                }
            }
        }

        public Vector3F getAngle() {
            return this.angle;
        }

        public boolean useItemItem() {
            return this.useItemItem;
        }

        public Material getItem() {
            return this.item;
        }
    }

    private Vector position = new Vector(0, 0, 0);
    private final Map<EquipmentSlot, BodyPart> slots = new EnumMap<>(EquipmentSlot.class);
    private boolean marker = true;
    private boolean invisible = true;
    private boolean small = false;
    private boolean arms = false;
    private boolean basePlate = true;
    private boolean staticRotation = false;
    private String customName = null;

    public ArmorStandPreset(ConfigurationSection section) {
        ConfigurationSection position = section.getConfigurationSection("position");
        if(position != null) {
            if(position.contains("x")) {
                this.position.setX(position.getDouble("x"));
            }
            if(position.contains("y")) {
                this.position.setY(position.getDouble("y"));
            }
            if(position.contains("z")) {
                this.position.setZ(position.getDouble("z"));
            }
        }
        ConfigurationSection bodyParts = section.getConfigurationSection("bodyParts");
        if(bodyParts != null) {
            for(String key : bodyParts.getKeys(false)) {
                try {
                    this.slots.put(EquipmentSlot.valueOf(key.toUpperCase()), new BodyPart(bodyParts.getConfigurationSection(key)));
                } catch(IllegalArgumentException ignored) {}
            }
        }
        if(section.isBoolean("marker")) {
            this.marker = section.getBoolean("marker");
        }
        if(section.isBoolean("invisible")) {
            this.invisible = section.getBoolean("invisible");
        }
        if(section.isBoolean("small")) {
            this.small = section.getBoolean("small");
        }
        if(section.isBoolean("arms")) {
            this.arms = section.getBoolean("arms");
        }
        if(section.isBoolean("basePlate")) {
            this.basePlate = section.getBoolean("basePlate");
        }
        if(section.isBoolean("staticRotation")) {
            this.staticRotation = section.getBoolean("staticRotation");
        }
        if(section.isString("customName")) {
            this.customName = section.getString("customName");
        }
    }

    public Vector getPosition() {
        return this.position;
    }

    public Map<EquipmentSlot, BodyPart> getSlots() {
        return this.slots;
    }

    public boolean isMarker() {
        return this.marker;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean isSmall() {
        return this.small;
    }

    public boolean isArms() {
        return this.arms;
    }

    public boolean isBasePlate() {
        return this.basePlate;
    }

    public boolean isStaticRotation() {
        return this.staticRotation;
    }

    public String getCustomName() {
        return this.customName;
    }
}