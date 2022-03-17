package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import io.github.fabricators_of_create.porting_lib.event.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.level.GameType;

import net.minecraft.world.level.block.Blocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.BlockUseBypassingItem;
import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	@Final
	@Shadow
	private ClientPacketListener connection;

	@Shadow
	private GameType localPlayerMode;

	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyReceiver(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
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

	@Inject(method = "useItemOn",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
	public void port_lib$useItemOn(LocalPlayer clientPlayerEntity, ClientLevel clientWorld, InteractionHand hand, BlockHitResult blockRayTraceResult, CallbackInfoReturnable<InteractionResult> cir) {
		if (clientPlayerEntity.getItemInHand(hand).getItem() instanceof UseFirstBehaviorItem first) {
			UseOnContext ctx = new UseOnContext(clientPlayerEntity, hand, blockRayTraceResult);
			InteractionResult result = first.onItemUseFirst(clientPlayerEntity.getItemInHand(hand), ctx);
			if (result != InteractionResult.PASS) {
				this.connection.send(new ServerboundUseItemOnPacket(hand, blockRayTraceResult));
				cir.setReturnValue(result);
			}
		}
	}

	@Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;createInteractionPacket(Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/world/InteractionHand;)Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;"), cancellable = true)
	public void port_lib$onEntityInteract(Player player, Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (this.localPlayerMode != GameType.SPECTATOR) { // don't fire for spectators to match non-specific EntityInteract
			InteractionResult cancelResult = EntityInteractCallback.EVENT.invoker().onEntityInteract(player, hand, target);
			if (cancelResult != null) cir.setReturnValue(cancelResult);
		}
	}

	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (((ItemStackExtensions)(Object)minecraft.player.getMainHandItem()).onBlockStartBreak(pos, minecraft.player)) cir.setReturnValue(false);
	}
}
