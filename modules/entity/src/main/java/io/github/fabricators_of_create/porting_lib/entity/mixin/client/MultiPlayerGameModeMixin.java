package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapOperation(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	public InteractionResult onEntityInteractAt(Entity instance, Player player, Vec3 vec3, InteractionHand hand, Operation<InteractionResult> original, Player p_105231_, Entity p_105232_, EntityHitResult p_105233_) {
		InteractionResult cancelResult = EntityHooks.onInteractEntityAt(player, instance, p_105233_, hand);
		if (cancelResult != null) return cancelResult;
		return original.call(instance, player, vec3, hand);
	}

	@WrapWithCondition(method = "method_41936", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean wrapBlockLeftClick(MultiPlayerGameMode instance, BlockPos pLoc, BlockPos other, Direction pFace, int i) {
		return !EntityHooks.onLeftClickBlock(this.minecraft.player, pLoc, pFace, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled();
	}

	@WrapWithCondition(method = "method_41935", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean continueWrapBlockLeftClick(MultiPlayerGameMode instance, BlockPos pLoc, BlockPos other, Direction pFace, int i) {
		return !EntityHooks.onLeftClickBlock(this.minecraft.player, pLoc, pFace, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled();
	}

	@Inject(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
	private void continueLeftClickBlock(BlockPos pPosBlock, Direction pDirectionFacing, CallbackInfoReturnable<Boolean> cir) {
		if (EntityHooks.onClientMineHold(this.minecraft.player, pPosBlock, pDirectionFacing).getUseItem() == TriState.FALSE)
			cir.setReturnValue(true);
	}

	@Unique
	private final ThreadLocal<PlayerInteractEvent.LeftClickBlock> capturedEvent = new ThreadLocal<>();

	@Inject(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1))
	public void port_lib$startDestroy(BlockPos loc, Direction face, CallbackInfoReturnable<Boolean> cir) {
		var e = EntityHooks.onLeftClickBlock(minecraft.player, loc, face, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
		capturedEvent.set(e);
	}

	@WrapWithCondition(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;attack(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
	private boolean cancelLeftClickAttack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
		return capturedEvent.get().getUseBlock() != TriState.FALSE;
	}

	@Inject(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"), cancellable = true)
	private void cancelUsePacket(BlockState blockState, BlockPos blockPos, Direction direction, int i, CallbackInfoReturnable<Packet<?>> cir) {
		if (capturedEvent.get().getUseItem() == TriState.FALSE)
			cir.setReturnValue(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction, i));
	}

	@Inject(method = "method_41929", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"
	), cancellable = true)
	private void onItemRightClick(InteractionHand hand, Player player, MutableObject<InteractionResult> result, int sequence, CallbackInfoReturnable<Packet<?>> cir, @Local ServerboundUseItemPacket packet) {
		InteractionResult cancelResult = EntityHooks.onItemRightClick(player, hand);
		if (cancelResult != null) {
			result.setValue(cancelResult);
			cir.setReturnValue(packet);
		}
	}

	@Inject(method = "method_41929", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V",
			shift = At.Shift.AFTER
	))
	private void onPlayerDestroyItem(InteractionHand hand, Player player, MutableObject<InteractionResult> result, int sequence, CallbackInfoReturnable<Packet> cir, @Local(index = 6) ItemStack destroyed, @Local(index = 8) ItemStack item) {
		if (item.isEmpty())
			EntityHooks.onPlayerDestroyItem(player, destroyed, hand);
	}

	@Inject(method = "performUseItemOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;",
			ordinal = 0
	), cancellable = true)
	private void onRightClickBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, @Local BlockPos pos) {
		PlayerInteractEvent.RightClickBlock event = EntityHooks.onRightClickBlock(player, hand, pos, hitResult);
		if (event.isCanceled()) {
			cir.setReturnValue(event.getCancellationResult());
		}
	}
}
