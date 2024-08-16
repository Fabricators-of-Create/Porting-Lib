package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.BambooSaplingBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BambooSaplingBlock.class)
public class BambooSaplingBlockMixin {
	@ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack toolActionIsSword(ItemStack original) {
		if (original.getItem() instanceof ItemAbilityItem)
			return original.canPerformAction(ItemAbilities.SWORD_DIG) ? original.getItem() instanceof SwordItem ? original : Items.IRON_SWORD.getDefaultInstance() : Items.AIR.getDefaultInstance();
		return original;
	}
}
