package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import io.github.fabricators_of_create.porting_lib.block.PlayerDestroyBlock;
import io.github.fabricators_of_create.porting_lib.item.BlockUseBypassingItem;
import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
	public void port_lib$useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.getItem() instanceof UseFirstBehaviorItem useFirst) {
			UseOnContext ctx = new UseOnContext(player, hand, hit);
			BlockPos pos = ctx.getClickedPos();
			BlockInWorld block = new BlockInWorld(ctx.getLevel(), pos, false);
			if (!player.getAbilities().mayBuild && !heldItem.hasAdventureModePlaceTagForBlock(BuiltInRegistries.BLOCK, block)) {
				cir.setReturnValue(InteractionResult.PASS);
			} else {
				Item item = heldItem.getItem();
				InteractionResult result = useFirst.onItemUseFirst(heldItem, ctx);
				if (result.shouldAwardStats()) {
					player.awardStat(Stats.ITEM_USED.get(item));
				}

				if (result != InteractionResult.PASS) {
					cir.setReturnValue(result);
				}
			}
		}
	}



	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (minecraft.player.getMainHandItem().onBlockStartBreak(pos, minecraft.player)) cir.setReturnValue(false);
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
}
