package me.ste.stevesseries.fancydrops

import com.comphenix.protocol.ProtocolLibrary
import me.ste.stevesseries.fancydrops.command.ReloadCommand
import me.ste.stevesseries.fancydrops.command.TestConfigCommand
import me.ste.stevesseries.fancydrops.item.FancyItem
import me.ste.stevesseries.fancydrops.listener.ItemListener
import me.ste.stevesseries.fancydrops.listener.PacketListener
import me.ste.stevesseries.fancydrops.listener.PlayerListener
import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Item
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.jar.JarFile
import java.util.logging.Level

class FancyDrops : JavaPlugin() {
    private val dataPath = this.dataFolder.toPath()

    private val legacyConfigFile = this.dataPath.resolve("config.yml")
    private val messagesFile = this.dataPath.resolve("messages.yml")

    private val presetsPath = this.dataPath.resolve("presets")

    internal lateinit var messagesConfiguration: FileConfiguration

    override fun onEnable() {
        this.reloadPluginConfiguration()

        this.server.pluginManager.registerEvents(ItemListener, this)
        this.server.pluginManager.registerEvents(PlayerListener, this)

        this.server.scheduler.runTaskTimer(this, Runnable {
            FancyItem.ITEMS.entries.removeIf {
                return@removeIf !it.value.update()
            }
        }, 0L, 1L)

        this.server.scheduler.runTaskTimer(this, Runnable {
            for (player in this.server.onlinePlayers) {
                val reachDistance = if (player.gameMode == GameMode.CREATIVE) 5.0 else 4.0
                val blockDistance = player.rayTraceBlocks(reachDistance * 2.0)?.hitPosition?.distance(player.eyeLocation.toVector()) ?: Double.MAX_VALUE
                for ((_, item) in FancyItem.ITEMS) {
                    for (stand in item.entities) {
                        val box = stand.customNameBoundingBox
                        if (box != null) {
                            val result = box.rayTrace(
                                player.eyeLocation.toVector(),
                                player.location.direction,
                                reachDistance
                            )
                            stand.setCustomNameObserverStatus(player, result != null && result.hitPosition.distance(player.eyeLocation.toVector()) < blockDistance)
                        }
                    }
                }
            }
        }, 0L, 5L)

        this.getCommand("fancydropsreload")!!.setExecutor(ReloadCommand(this))
        this.getCommand("fancydropstestconfig")!!.setExecutor(TestConfigCommand(this))

        for(listener in PacketListener(this).listeners) {
            ProtocolLibrary.getProtocolManager().addPacketListener(listener)
        }

        for(world in Bukkit.getWorlds()) {
            for(item in world.getEntitiesByClass(Item::class.java)) {
                val preset = FancyItemPreset.matchPreset(item.itemStack)
                if(preset != null) {
                    val fancyItem = FancyItem(preset, item)
                    for(player in Bukkit.getOnlinePlayers()) {
                        fancyItem.addObserver(player)
                    }
                    FancyItem.ITEMS[item.uniqueId] = fancyItem
                }
            }
        }
    }

    override fun onDisable() {
        this.server.scheduler.cancelTasks(this)

        for(player in Bukkit.getOnlinePlayers()) {
            for((_, item) in FancyItem.ITEMS) {
                item.removeObserver(player)
            }
        }
    }

    private fun convertLegacyConfiguration() {
        if (Files.isRegularFile(this.legacyConfigFile)) {
            try {
                val config = YamlConfiguration()
                config.load(InputStreamReader(Files.newInputStream(this.legacyConfigFile)))

                this.messagesConfiguration = YamlConfiguration()
                val legacyMessages = config.getConfigurationSection("messages")
                if (legacyMessages != null) {
                    for (key in legacyMessages.getKeys(false)) {
                        if (legacyMessages.isString(key)) {
                            this.messagesConfiguration.set(key, legacyMessages.getString(key))
                        }
                    }
                }
                Files.write(this.messagesFile, this.messagesConfiguration.saveToString().encodeToByteArray())

                if (!Files.isDirectory(this.presetsPath)) {
                    Files.createDirectory(this.presetsPath)
                }

                val legacyPresets = config.getConfigurationSection("presets")
                if (legacyPresets != null) {
                    val keys = legacyPresets.getKeys(false)

                    var priority = keys.size

                    for (key in keys) {
                        val presetConfig = YamlConfiguration()
                        presetConfig.set("priority", --priority)
                        val legacySection = legacyPresets.getConfigurationSection(key)!!
                        for((key2, value) in legacySection.getValues(true)) {
                            presetConfig.set(key2, value)
                        }
                        Files.write(this.presetsPath.resolve("$key.yml"), presetConfig.saveToString().encodeToByteArray())
                    }
                }

                Files.delete(this.legacyConfigFile)

                this.logger.log(Level.INFO, "The legacy configuration file has been converted to the new configuration system")
            } catch (exception: Exception) {
                Files.move(
                    this.legacyConfigFile,
                    this.legacyConfigFile.resolveSibling("${this.legacyConfigFile.fileName}.old")
                )
                this.logger.log(Level.SEVERE, "Failed to convert the legacy configuration file!", exception)
            }
        }
    }

    private fun saveConfigurationDefaults() {
        if(!Files.isRegularFile(this.messagesFile)) {
            this.saveResource("messages.yml", false)
        }

        if(!Files.isDirectory(this.presetsPath)) {
            Files.createDirectory(this.presetsPath)

            val entries = JarFile(this.file).entries()
            while (entries.hasMoreElements()) {
                val element = entries.nextElement()
                if (element.name.startsWith("presets/") && element.name.endsWith(".yml")) {
                    this.saveResource(element.name, false)
                }
            }
        }
    }

    internal fun reloadPluginConfiguration() {
        if (!Files.isDirectory(this.dataPath)) {
            Files.createDirectory(this.dataPath)
        }

        this.convertLegacyConfiguration()
        this.saveConfigurationDefaults()

        this.messagesConfiguration = YamlConfiguration()
        this.messagesConfiguration.load(InputStreamReader(Files.newInputStream(this.messagesFile)))

        val presets: MutableList<FancyItemPreset> = ArrayList()
        for(file in Files.list(this.presetsPath)) {
            if(Files.isRegularFile(file) && file.fileName.toString().endsWith(".yml")) {
                val presetConfig = YamlConfiguration()
                presetConfig.load(InputStreamReader(Files.newInputStream(file)))
                presets += FancyItemPreset(file, presetConfig)
            }
        }
        presets.sortWith { a, b ->
            return@sortWith b.priority - a.priority
        }
        FancyItemPreset.PRESETS.clear()
        for(preset in presets) {
            FancyItemPreset.PRESETS.add(preset)
        }
    }
}