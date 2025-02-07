package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents.Teleport.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket.Flag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.world.ServerWorld;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {
	@Inject(method = "performTeleport", at = @At("HEAD"), cancellable = true)
	private static void port_lib$performTeleport(ServerCommandSource source, Entity entity, ServerWorld level,
												 double x, double y, double z, Set<Flag> relativeList, float yaw,
												 float pitch, @Nullable @Coerce Object facing, CallbackInfo ci) {
		EntityTeleportEvent event = new EntityTeleportEvent(entity, x, y, z);
		event.sendEvent();
		if (event.isCanceled()) {
			ci.cancel();
		}
	}
}
