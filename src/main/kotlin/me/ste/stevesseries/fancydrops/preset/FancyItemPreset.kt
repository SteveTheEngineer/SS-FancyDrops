package me.ste.stevesseries.fancydrops.preset

import me.ste.stevesseries.fancydrops.event.FancyItemPresetLookupEvent
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.regex.Pattern

class FancyItemPreset(section: ConfigurationSection) {
    enum class RightClickPickup {
        DISABLED,
        ENABLED,
        REQUIRED
    }

    companion object {
        val PRESETS: MutableList<FancyItemPreset> = LinkedList()

        fun matchPreset(stack: ItemStack): FancyItemPreset? {
            var result: FancyItemPreset? = null
            for (preset in this.PRESETS) {
                var materialMatches = preset.matchMaterials.size <= 0
                if (!materialMatches) {
                    for (pattern in preset.matchMaterials) {
                        if (pattern.matcher(stack.type.name).matches()) {
                            materialMatches = true
                            break
                        }
                    }
                }
                if (!materialMatches) {
                    continue
                }

                var displayNameMatches = preset.matchDisplayNames.size <= 0
                if (!displayNameMatches) {
                    val meta = stack.itemMeta
                    if (meta == null || !meta.hasDisplayName()) {
                        continue
                    }
                    for (pattern in preset.matchDisplayNames) {
                        if (pattern.matcher(meta.displayName).matches()) {
                            displayNameMatches = true
                            break
                        }
                    }
                }
                if (!displayNameMatches) {
                    continue
                }

                if (stack.amount < preset.minAmount || stack.amount > preset.maxAmount) {
                    continue
                }

                result = preset
                break
            }
            val event = FancyItemPresetLookupEvent(result)
            Bukkit.getPluginManager().callEvent(event)
            return event.matched
        }
    }

    var priority: Int = 0
        private set

    val entities: MutableMap<String, ArmorStandPreset> = HashMap()

    val matchMaterials: MutableSet<Pattern> = HashSet()
    val matchDisplayNames: MutableSet<Pattern> = HashSet()

    var minAmount: Int = 0
        private set
    var maxAmount: Int = 127
        private set

    var rightClickPickup = RightClickPickup.DISABLED
        private set

    init {
        val entities = section.getConfigurationSection("entities")
        if (entities != null) {
            for (key in entities.getKeys(false)) {
                this.entities[key] = ArmorStandPreset(entities.getConfigurationSection(key)!!)
            }
        }

        val match = section.getConfigurationSection("match")
        if (match != null) {
            if (match.isList("materials")) {
                for (pattern in match.getStringList("materials")) {
                    this.matchMaterials.add(Pattern.compile(pattern))
                }
            }
            if (match.isList("displayNames")) {
                for (pattern in match.getStringList("displayNames")) {
                    this.matchDisplayNames.add(Pattern.compile(pattern))
                }
            }
            if (match.isInt("minAmount")) {
                this.minAmount = match.getInt("minAmount")
            }
            if (match.isInt("maxAmount")) {
                this.maxAmount = match.getInt("maxAmount")
            }
        }

        val settings = section.getConfigurationSection("settings")
        if (settings != null) {
            if (settings.isString("rightClickPickup")) {
                this.rightClickPickup = RightClickPickup.valueOf(settings.getString("rightClickPickup")!!)
            }
        }

        if (section.isInt("priority")) {
            this.priority = section.getInt("priority")
        }
    }
}