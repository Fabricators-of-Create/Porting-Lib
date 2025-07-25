package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.SneakBypassUseItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.UseFirstBehaviorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	@Inject(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
			),
			cancellable = true
	)
	public void onItemFirstUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
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

	@ModifyVariable(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), ordinal = 1)
	private boolean checkShouldSneakBypassUse(boolean original, @Local(argsOnly = true) ServerPlayer player, @Local(argsOnly = true) Level level, @Local BlockPos pos) {
		return original && !(
				(player.getMainHandItem().getItem() instanceof SneakBypassUseItem mainBypassUseItem
						&& mainBypassUseItem.doesSneakBypassUse(player.getMainHandItem(), level, pos, player))
				&&
				(player.getOffhandItem().getItem() instanceof SneakBypassUseItem offhandBypassUseItem
						&& offhandBypassUseItem.doesSneakBypassUse(player.getOffhandItem(), level, pos, player))
		);
	}
}
