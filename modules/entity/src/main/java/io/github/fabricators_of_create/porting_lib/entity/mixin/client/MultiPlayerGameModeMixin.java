package io.github.fabricators_of_create.porting_lib.entity.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private GameType localPlayerMode;

	@Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;createInteractionPacket(Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/world/InteractionHand;)Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;"), cancellable = true)
	public void port_lib$onEntityInteract(Player player, Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (this.localPlayerMode != GameType.SPECTATOR) { // don't fire for spectators to match non-specific EntityInteract
			InteractionResult cancelResult = EntityInteractCallback.EVENT.invoker().onEntityInteract(player, hand, target);
			if (cancelResult != null) cir.setReturnValue(cancelResult);
		}
	}

	@WrapWithCondition(method = "method_41936", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean wrapBlockLeftClick(MultiPlayerGameMode instance, BlockPos pLoc, BlockPos other, Direction pFace, int i) {
		PlayerInteractionEvents.LeftClickBlock event = new PlayerInteractionEvents.LeftClickBlock(this.minecraft.player, pLoc, pFace);
		event.sendEvent();
		return !event.isCanceled();
	}

	@WrapWithCondition(method = "method_41935", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean continueWrapBlockLeftClick(MultiPlayerGameMode instance,  BlockPos pLoc, BlockPos other, Direction pFace, int i) {
		PlayerInteractionEvents.LeftClickBlock event = new PlayerInteractionEvents.LeftClickBlock(this.minecraft.player, pLoc, pFace);
		event.sendEvent();
		return !event.isCanceled();
	}

	@Inject(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onDestroyBlock(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
	private void continueLeftClickBlock(BlockPos pPosBlock, Direction pDirectionFacing, CallbackInfoReturnable<Boolean> cir) {
		PlayerInteractionEvents.LeftClickBlock event = new PlayerInteractionEvents.LeftClickBlock(this.minecraft.player, pPosBlock, pDirectionFacing);
		event.sendEvent();
		if (event.getUseItem() == BaseEvent.Result.DENY) cir.setReturnValue(true);
	}

	private PlayerInteractionEvents.LeftClickBlock capturedEvent;

	@Inject(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1))
	public void port_lib$startDestroy(BlockPos loc, Direction face, CallbackInfoReturnable<Boolean> cir) {
		capturedEvent = new PlayerInteractionEvents.LeftClickBlock(minecraft.player, loc, face);
		capturedEvent.sendEvent();
	}

	@WrapWithCondition(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;attack(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
	private boolean cancelLeftClickAttack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
		return capturedEvent.getUseBlock() != BaseEvent.Result.DENY;
	}

	@Inject(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroyProgress(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"), cancellable = true)
	private void cancelUsePacket(BlockState blockState, BlockPos blockPos, Direction direction, int i, CallbackInfoReturnable<Packet> cir) {
		if (capturedEvent.getUseItem() == BaseEvent.Result.DENY) cir.setReturnValue(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction, i));
	}
}
