package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.nio.ByteBuffer;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.mojang.datafixers.util.Pair;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin {
	@Shadow
	private ByteBuffer buffer;

	@Inject(
			method = "popNextBuffer",
			at = @At(
					value = "INVOKE",
					target = "Ljava/nio/ByteBuffer;clear()Ljava/nio/ByteBuffer;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$bufferOrder(CallbackInfoReturnable<Pair<BufferBuilder.DrawArrayParameters, ByteBuffer>> cir, BufferBuilder.DrawArrayParameters bufferbuilder$drawstate, ByteBuffer byteBuffer) {
		byteBuffer.order(this.buffer.order()); // Fix incorrect byte order
	}
}
