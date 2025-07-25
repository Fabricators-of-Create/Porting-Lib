package io.github.fabricators_of_create.porting_lib.item.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.BlockUseBypassingItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.SneakBypassUseItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.UseFirstBehaviorItem;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@ModifyReceiver(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;"))
	public BlockState bypassBlockUse(BlockState instance, ItemStack itemStack, Level level, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
		Item held = player.getItemInHand(hand).getItem();
		if (held instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand))
				return Blocks.BARRIER.defaultBlockState();
		} else if (held instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockUseBypassingItem bypassing) {
			if (bypassing.shouldBypass(level.getBlockState(blockHitResult.getBlockPos()), blockHitResult.getBlockPos(), level, player, hand)) return Blocks.BARRIER.defaultBlockState();
		}
		return instance;
	}

	@Inject(method = "performUseItemOn",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
	public void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.getItem() instanceof UseFirstBehaviorItem useFirst) {
			UseOnContext ctx = new UseOnContext(player, hand, hit);
			BlockPos pos = ctx.getClickedPos();
			BlockInWorld block = new BlockInWorld(ctx.getLevel(), pos, false);
			if (!player.getAbilities().mayBuild && !heldItem.canPlaceOnBlockInAdventureMode(block)) {
				cir.setReturnValue(InteractionResult.PASS);
			} else {
				Item item = heldItem.getItem();
				InteractionResult result = useFirst.onItemUseFirst(heldItem, ctx);
				if (result.indicateItemUse()) {
					player.awardStat(Stats.ITEM_USED.get(item));
				}

				if (result != InteractionResult.PASS) {
					cir.setReturnValue(result);
				}
			}
		}
	}

	@WrapOperation(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSecondaryUseActive()Z")))
	private boolean checkDoesSneakBypassUse(ItemStack instance, Operation<Boolean> original, @Local(argsOnly = true) LocalPlayer player, @Local BlockPos pos) {
		if (instance.getItem() instanceof SneakBypassUseItem bypassUseItem)
			return bypassUseItem.doesSneakBypassUse(instance, player.level(), pos, player);

		return original.call(instance);
	}
}
