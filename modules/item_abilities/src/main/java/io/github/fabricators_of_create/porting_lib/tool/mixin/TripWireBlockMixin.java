package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripWireBlock.class)
public class TripWireBlockMixin {
	@ModifyExpressionValue(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private boolean supportsToolAction(boolean original, Level world, BlockPos pos, BlockState state, Player player) {
		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof ItemAbilityItem toolActionItem)
			return toolActionItem.canPerformAction(stack, ItemAbilities.SHEARS_DISARM);
		return original;
	}
}
