package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	public abstract boolean isCreative();

	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"), cancellable = true)
	public void port_lib$blockBreak(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int i, CallbackInfo ci, @Share("event")LocalRef<PlayerInteractEvent.LeftClickBlock> eventRef) {
		PlayerInteractEvent.LeftClickBlock event = EntityHooks.onLeftClickBlock(player, pos, direction, action);
		eventRef.set(event);
		if (event.isCanceled()) {
			ci.cancel();
		}
	}

	@WrapWithCondition(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;attack(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
	private boolean canAttack(BlockState instance, Level level, BlockPos blockPos, Player player, @Share("event")LocalRef<PlayerInteractEvent.LeftClickBlock> eventRef) {
		return eventRef.get().getUseBlock() != TriState.FALSE;
	}

	@Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 0), cancellable = true)
	private void onItemRightClick(ServerPlayer player, Level level, ItemStack itemStack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult cancelResult = EntityHooks.onItemRightClick(player, hand);
		if (cancelResult != null)
			cir.setReturnValue(cancelResult);
	}

	@ModifyExpressionValue(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;isEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"))
	private boolean onRightClickBlock(
			boolean original, ServerPlayer player, Level level, ItemStack itemStack, InteractionHand hand, BlockHitResult hitResult, @Local(index = 6) BlockPos pos,
			@Share("event") LocalRef<PlayerInteractEvent.RightClickBlock> eventRef
			) {
		if (original) {
			PlayerInteractEvent.RightClickBlock event = EntityHooks.onRightClickBlock(player, hand, pos, hitResult);
			eventRef.set(event);
			if (event.isCanceled())
				return false;
		}
		return original;
	}

	@ModifyReturnValue(method = "useItemOn", at = @At(value = "RETURN", ordinal = 0))
	private InteractionResult changeRightClickResult(InteractionResult original, @Share("event") LocalRef<PlayerInteractEvent.RightClickBlock> eventRef) {
		var event = eventRef.get();
		if (event.isCanceled())
			return event.getCancellationResult();
		return original;
	}
}
