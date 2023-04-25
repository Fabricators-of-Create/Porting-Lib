package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.PortingHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(targets = "net/minecraft/server/network/ServerGamePacketListenerImpl$1")
public class ServerGamePacketListenerImplMixin {
	@Inject(method = "method_33898", at = @At("RETURN"), cancellable = true)
	private static void entitySpecificInteract(Vec3 vec3, ServerPlayer player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult onInteractEntityAtResult = PortingHooks.onInteractEntityAt(player, entity, vec3, hand);
		if (onInteractEntityAtResult != null) cir.setReturnValue(onInteractEntityAtResult);
	}
}
