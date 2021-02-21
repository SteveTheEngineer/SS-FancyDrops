package me.ste.stevesseries.fancydrops.packet

import com.comphenix.protocol.events.PacketContainer

abstract class Packet {
    abstract val container: PacketContainer
}