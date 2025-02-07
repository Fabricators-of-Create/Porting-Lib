package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import io.github.fabricators_of_create.porting_lib.event.common.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib.util.PlayerDestroyBlock;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MultiPlayerGameModeMixin {
	@Final
	@Shadow
	private ClientPlayNetworkHandler connection;

	@Shadow
	private GameMode localPlayerMode;

	@Shadow
	@Final
	private MinecraftClient minecraft;

	@ModifyReceiver(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	public BlockState port_lib$bypassBlockUse(BlockState result, World level, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
		Item held = player.getStackInHand(hand).getItem();
		if (held instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand))
				return Blocks.BARRIER.getDefaultState();
		} else if (held instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand)) return Blocks.BARRIER.getDefaultState();
		}
		return result;
	}

	@Inject(method = "useItemOn",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
	public void port_lib$useItemOn(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, Hand hand, BlockHitResult blockRayTraceResult, CallbackInfoReturnable<ActionResult> cir) {
		if (clientPlayerEntity.getStackInHand(hand).getItem() instanceof UseFirstBehaviorItem first) {
			ItemUsageContext ctx = new ItemUsageContext(clientPlayerEntity, hand, blockRayTraceResult);
			ActionResult result = first.onItemUseFirst(clientPlayerEntity.getStackInHand(hand), ctx);
			if (result != ActionResult.PASS) {
				this.connection.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockRayTraceResult));
				cir.setReturnValue(result);
			}
		}
	}

	@Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;createInteractionPacket(Lnet/minecraft/world/entity/Entity;ZLnet/minecraft/world/InteractionHand;)Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;"), cancellable = true)
	public void port_lib$onEntityInteract(PlayerEntity player, Entity target, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (this.localPlayerMode != GameMode.SPECTATOR) { // don't fire for spectators to match non-specific EntityInteract
			ActionResult cancelResult = EntityInteractCallback.EVENT.invoker().onEntityInteract(player, hand, target);
			if (cancelResult != null) cir.setReturnValue(cancelResult);
		}
	}

	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (((ItemStackExtensions)(Object)minecraft.player.getMainHandStack()).onBlockStartBreak(pos, minecraft.player)) cir.setReturnValue(false);
	}

	@Inject(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1))
	public void port_lib$startDestroy(BlockPos loc, Direction face, CallbackInfoReturnable<Boolean> cir) {
		BlockEvents.LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(minecraft.player, loc, face);
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$playerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, World level, BlockState blockstate, Block block, FluidState fluidstate) {
		if (blockstate.getBlock() instanceof PlayerDestroyBlock destroyBlock) {
			boolean flag = destroyBlock.onDestroyedByPlayer(blockstate, level, pos, minecraft.player, false, fluidstate);
			if (flag) {
				block.onBroken(level, pos, blockstate);
			}

			cir.setReturnValue(flag);
		}
	}
}
