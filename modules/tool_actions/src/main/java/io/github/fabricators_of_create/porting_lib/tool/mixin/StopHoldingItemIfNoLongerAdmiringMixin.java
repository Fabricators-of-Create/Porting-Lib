package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib.tool.addons.ToolActionItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.StopHoldingItemIfNoLongerAdmiring;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StopHoldingItemIfNoLongerAdmiring.class)
public class StopHoldingItemIfNoLongerAdmiringMixin {
	@ModifyExpressionValue(method = "method_47299", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private static boolean isToolActionBlocking(boolean original, ServerLevel level, Piglin piglin) {
		ItemStack offHand = piglin.getOffhandItem();
		if (offHand.getItem() instanceof ToolActionItem)
			return offHand.canPerformAction(ToolActions.SHIELD_BLOCK);
		return original;
	}
}
