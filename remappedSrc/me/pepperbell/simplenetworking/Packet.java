package me.pepperbell.simplenetworking;

import net.minecraft.network.PacketByteBuf;

public interface Packet {
	void encode(PacketByteBuf buf);
}
