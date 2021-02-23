package me.ste.stevesseries.fancydrops.event

import me.ste.stevesseries.fancydrops.preset.FancyItemPreset
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class FancyItemPresetLookupEvent(val stack: ItemStack, matched: FancyItemPreset?) : Event() {
    var pluginModified: Boolean = false
        private set

    var matched: FancyItemPreset? = matched
        set(value) {
            this.pluginModified = true
            field = value
        }

    companion object {
        private val HANDLER_LIST = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers(): HandlerList = HANDLER_LIST
}