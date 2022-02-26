package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.network.FriendlyByteBuf;

public interface ClientboundAddEntityPacketExtensions {
	FriendlyByteBuf port_lib$getExtraDataBuf();
}
