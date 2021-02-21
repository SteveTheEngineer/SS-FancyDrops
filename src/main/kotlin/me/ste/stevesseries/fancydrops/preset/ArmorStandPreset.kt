package me.ste.stevesseries.fancydrops.preset

import com.comphenix.protocol.wrappers.Vector3F
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import java.util.*

class ArmorStandPreset(section: ConfigurationSection) {
    class BodyPart(section: ConfigurationSection) {
        var angle = Vector3F(0F, 0F, 0F)
        var useItemItem = false
        var item: Material? = null

        init {
            val angle = section.getConfigurationSection("angle")
            if (angle != null) {
                if (angle.contains("x")) {
                    this.angle.x = angle.getDouble("x").toFloat()
                }
                if (angle.contains("y")) {
                    this.angle.y = angle.getDouble("y").toFloat()
                }
                if (angle.contains("z")) {
                    this.angle.z = angle.getDouble("z").toFloat()
                }
            }

            if (section.isString("item")) {
                val value = section.getString("item")!!
                if (value.equals("\$item", true)) {
                    this.useItemItem = true
                } else {
                    this.item = Material.getMaterial(value)
                }
            }
        }
    }

    var position = Vector()
        private set
    var slots: MutableMap<EquipmentSlot, BodyPart> = EnumMap(EquipmentSlot::class.java)
        private set
    var marker = true
        private set
    var invisible = true
        private set
    var small = false
        private set
    var arms = false
        private set
    var basePlate = true
        private set
    var staticRotation = false
        private set
    var customName: String? = null
        private set

    init {
        val position = section.getConfigurationSection("position")
        if (position != null) {
            if (position.contains("x")) {
                this.position.x = position.getDouble("x")
            }
            if (position.contains("y")) {
                this.position.y = position.getDouble("y")
            }
            if (position.contains("z")) {
                this.position.z = position.getDouble("z")
            }
        }

        val bodyParts = section.getConfigurationSection("bodyParts")
        if (bodyParts != null) {
            for (key in bodyParts.getKeys(false)) {
                this.slots[EquipmentSlot.valueOf(key.toUpperCase())] =
                    BodyPart(bodyParts.getConfigurationSection(key)!!)
            }
        }

        if(section.isBoolean("marker")) {
            this.marker = section.getBoolean("marker")
        }
        if(section.isBoolean("invisible")) {
            this.invisible = section.getBoolean("invisible")
        }
        if(section.isBoolean("small")) {
            this.small = section.getBoolean("small")
        }
        if(section.isBoolean("arms")) {
            this.arms = section.getBoolean("arms")
        }
        if(section.isBoolean("basePlate")) {
            this.basePlate = section.getBoolean("basePlate")
        }
        if(section.isBoolean("staticRotation")) {
            this.staticRotation = section.getBoolean("staticRotation")
        }
        if(section.isString("customName")) {
            this.customName = section.getString("customName")
        }
    }
}