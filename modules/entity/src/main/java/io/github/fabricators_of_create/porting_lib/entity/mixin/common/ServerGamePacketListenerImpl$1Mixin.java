package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/server/network/ServerGamePacketListenerImpl$1")
public class ServerGamePacketListenerImpl$1Mixin {
	@Inject(method = "method_33898", at = @At("HEAD"))
	private static void onInteractEntityAt(Vec3 vec3, ServerPlayer player, Entity entity, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult onInteractEntityAtResult = EntityHooks.onInteractEntityAt(player, entity, vec3, interactionHand);
		if (onInteractEntityAtResult != null) cir.setReturnValue(onInteractEntityAtResult);
	}
}
