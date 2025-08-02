package io.github.fabricators_of_create.porting_lib.attributes.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import net.minecraft.world.entity.player.Abilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	@WrapOperation(method = "handleMovePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
	private boolean mayFly1(Abilities instance, Operation<Boolean> original) {
		return this.player.port_lib$mayFly(instance, original);
	}

	@WrapOperation(method = "handlePlayerAbilities", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;mayfly:Z"))
	private boolean mayFly2(Abilities instance, Operation<Boolean> original) {
		return this.player.port_lib$mayFly(instance, original);
	}
}
