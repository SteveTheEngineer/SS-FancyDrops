package me.ste.stevesseries.fancydrops.command

import me.ste.stevesseries.fancydrops.FancyDrops
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

class TestConfigCommand(private val plugin: FancyDrops) : CommandExecutor {
    private fun sendFormattedMessage(target: CommandSender, messageId: String, vararg args: Any?) {
        val raw = this.plugin.messagesConfiguration.getString(messageId)
        val colorized = ChatColor.translateAlternateColorCodes('&', raw)
        val formatted = String.format(colorized, *args)

        target.sendMessage(formatted)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender.hasPermission("stevesseries.fancydrops.reload")) {
            this.sendFormattedMessage(sender, "running-material-test")

            // Material coverage test
            val unmatched = mutableSetOf<Material>()
            var total = 0

            for (material in Material.values()) {
                // Ignore non-items and air. Air cannot be dropped as an item
                if (!material.isItem || material == Material.AIR) {
                    continue
                }
                total++

                val stack = ItemStack(material)
                val preset = FancyItemPreset.matchPreset(stack)

                if (preset == null) {
                    unmatched += material
                }
            }

            if (unmatched.isEmpty()) {
                this.sendFormattedMessage(sender, "material-test-success")
            } else {
                val matched = total - unmatched.size

                this.sendFormattedMessage(sender, "material-test-fail", floor(matched.toDouble() / total.toDouble() * 100.0).toInt(), matched, total, unmatched.joinToString())
            }

            // Typo test
            this.sendFormattedMessage(sender, "running-typo-test")

            for (preset in FancyItemPreset.PRESETS) {
                for (pattern in preset.matchMaterials) {
                    val name = pattern.pattern()

                    if (name.any { it.isLowerCase() }) {
                        this.sendFormattedMessage(sender, "typo-test-warning", name, preset.file.fileName.toString())
                    }
                }
            }

            this.sendFormattedMessage(sender, "typo-test-success")

            // Unused material name test
            this.sendFormattedMessage(sender, "running-unused-test")

            var unusedEntries = false

            for (preset in FancyItemPreset.PRESETS) {
                pattern@ for (pattern in preset.matchMaterials) {
                    for (material in Material.values()) {
                        if (pattern.matcher(material.name).matches()) {
                            break@pattern
                        }
                    }

                    this.sendFormattedMessage(sender, "unused-test-error", pattern.pattern(), preset.file.fileName.toString())
                    unusedEntries = true
                }
            }

            if (!unusedEntries) {
                this.sendFormattedMessage(sender, "unused-test-success")
            } else {
                this.sendFormattedMessage(sender, "unused-test-fail")
            }

            // Duplicate material name test
            this.sendFormattedMessage(sender, "running-duplicate-test")

            var duplicates = false

            for (preset in FancyItemPreset.PRESETS) {
                val uniqueNames = mutableSetOf<String>()
                val duplicateNames = mutableSetOf<String>()

                for (pattern in preset.matchMaterials) {
                    val name = pattern.pattern()

                    if (name in duplicateNames) {
                        continue
                    }

                    if (name in uniqueNames) {
                        this.sendFormattedMessage(sender, "duplicate-test-error", name, preset.file.fileName.toString())
                        duplicates = true
                        duplicateNames += name
                        continue
                    }
                }
            }

            if (!duplicates) {
                this.sendFormattedMessage(sender, "duplicate-test-success")
            } else {
                this.sendFormattedMessage(sender, "duplicate-test-fail")
            }
        } else {
            this.sendFormattedMessage(sender, "no-permission")
        }
        return true
    }
}