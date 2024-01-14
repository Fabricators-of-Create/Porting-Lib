package io.github.fabricators_of_create.porting_lib.mixin.common;


import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.TimeoutException;
import net.minecraft.network.Connection;
import net.minecraft.network.SkipPacketException;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

	@Shadow @Final private static Logger LOGGER;

	@Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V"))
	private void on_exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo ci) {
		if (throwable instanceof SkipPacketException) return;
		if (throwable instanceof TimeoutException) return;
		LOGGER.error("Exception occurred in netty pipeline", throwable);
	}
}
