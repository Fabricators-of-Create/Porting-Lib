package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.phys.EntityHitResult;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.block.PlayerDestroyBlock;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.event.common.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerInteractionEvents;
import io.github.fabricators_of_create.porting_lib.item.BlockUseBypassingItem;
import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Shadow
	private GameType localPlayerMode;

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyReceiver(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	public BlockState port_lib$bypassBlockUse(BlockState result, Level level, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		Item held = player.getItemInHand(hand).getItem();
		if (held instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand))
				return Blocks.BARRIER.defaultBlockState();
		} else if (held instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand)) return Blocks.BARRIER.defaultBlockState();
		}
		return result;
	}

	@Inject(method = "performUseItemOn",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
	public void port_lib$useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockRayTraceResult, CallbackInfoReturnable<InteractionResult> cir, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.getItem() instanceof UseFirstBehaviorItem first) {
			UseOnContext ctx = new UseOnContext(player, hand, blockRayTraceResult);
			BlockPos blockpos = ctx.getClickedPos();
			BlockInWorld blockinworld = new BlockInWorld(ctx.getLevel(), blockpos, false);
			if (!player.getAbilities().mayBuild && !heldItem.hasAdventureModePlaceTagForBlock(ctx.getLevel().registryAccess().registryOrThrow(Registries.BLOCK), blockinworld)) {
				cir.setReturnValue(InteractionResult.PASS);
			} else {
				if (eventRef.get().getUseItem() != BaseEvent.Result.DENY) {
					Item item = heldItem.getItem();
					InteractionResult interactionresult = first.onItemUseFirst(heldItem, ctx);
					if (interactionresult.shouldAwardStats()) {
						player.awardStat(Stats.ITEM_USED.get(item));
					}

					cir.setReturnValue(interactionresult);
				}
			}
		}
	}

	@WrapOperation(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult modifyUseResult(BlockState state, Level level, Player player, InteractionHand hand, BlockHitResult result, Operation<InteractionResult> operation, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		var event = eventRef.get();
		if (event.getUseBlock() == BaseEvent.Result.ALLOW || (event.getUseBlock() != BaseEvent.Result.DENY)) {
			return operation.call(state, level, player, hand, result);
		}
		return InteractionResult.FAIL;
	}

	@Inject(method = "performUseItemOn", at = @At("HEAD"), cancellable = true)
	private void onBlockRightClick(LocalPlayer player, InteractionHand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		PlayerInteractionEvents.RightClickBlock event = PortingHooks.onRightClickBlock(player, hand, blockHitResult.getBlockPos(), blockHitResult);
		if (event.isCanceled()) {
			cir.setReturnValue(event.getCancellationResult());
		}
		eventRef.set(event);
	}

	@Inject(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 2), cancellable = true)
	private void shouldCancelRightClick(LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		if (eventRef.get().getUseItem() == BaseEvent.Result.DENY) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	@ModifyExpressionValue(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 2))
	private boolean allowRightClick(boolean original, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		if (eventRef.get().getUseItem() == BaseEvent.Result.ALLOW)
			return false;
		return original;
	}

	@ModifyExpressionValue(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemCooldowns;isOnCooldown(Lnet/minecraft/world/item/Item;)Z"))
	private boolean allowRightClick2(boolean original, @Share("right_click_block") LocalRef<PlayerInteractionEvents.RightClickBlock> eventRef) {
		if (eventRef.get().getUseItem() == BaseEvent.Result.ALLOW)
			return false;
		return original;
	}

	@Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onEntitySpecificInteract(Player player, Entity target, EntityHitResult ray, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult cancelResult = PortingHooks.onInteractEntityAt(player, target, ray, hand);
		if(cancelResult != null) cir.setReturnValue(cancelResult);
	}

	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (minecraft.player.getMainHandItem().onBlockStartBreak(pos, minecraft.player)) cir.setReturnValue(false);
	}

	@WrapWithCondition(method = "method_41936", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
	private boolean wrapBlockLeftClick(MultiPlayerGameMode instance,  BlockPos pLoc, BlockPos other, Direction pFace, int i) {
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

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$playerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, Level level, BlockState blockstate, Block block, FluidState fluidstate) {
		if (blockstate.getBlock() instanceof PlayerDestroyBlock destroyBlock) {
			boolean flag = destroyBlock.onDestroyedByPlayer(blockstate, level, pos, minecraft.player, false, fluidstate);
			if (flag) {
				block.destroy(level, pos, blockstate);
			}

			cir.setReturnValue(flag);
		}
	}

	@Inject(method = "method_41929", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"), cancellable = true)
	private void rightClickItem(InteractionHand hand, Player player, MutableObject<InteractionResult> mutableObject, int i, CallbackInfoReturnable<Packet> cir, @Local(index = 5)ServerboundUseItemPacket serverbounduseitempacket) {
		InteractionResult cancelResult = PortingHooks.onItemRightClick(player, hand);
		if (cancelResult != null) {
			mutableObject.setValue(cancelResult);
			cir.setReturnValue(serverbounduseitempacket);
		}
	}
}
