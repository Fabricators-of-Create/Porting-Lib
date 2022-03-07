package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.EntityEvents.Teleport.EntityTeleportEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument;
import net.minecraft.server.commands.TeleportCommand;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandMixin {
	@Inject(method = "performTeleport", at = @At("HEAD"), cancellable = true)
	private static void port_lib$performTeleport(CommandSourceStack source, Entity entity, ServerLevel level,
												 double x, double y, double z, Set<RelativeArgument> relativeList, float yaw,
												 float pitch, @Nullable @Coerce Object facing, CallbackInfo ci) {
		EntityTeleportEvent event = new EntityTeleportEvent(entity, x, y, z);
		event.sendEvent();
		if (event.isCanceled()) {
			ci.cancel();
		}
	}
}
