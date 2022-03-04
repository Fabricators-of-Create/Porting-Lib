package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.network.FriendlyByteBuf;

public interface ClientboundAddEntityPacketExtensions {
	default FriendlyByteBuf getExtraDataBuf() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
