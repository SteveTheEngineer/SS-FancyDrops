package me.ste.stevesseries.fancydrops.extensions

import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher

val WrappedDataWatcher.dataValues get() = this.watchableObjects.map { WrappedDataValue(it.index, it.watcherObject.serializer, it.rawValue) }
