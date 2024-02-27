package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.CustomArrowItem;
import io.github.fabricators_of_create.porting_lib.item.InfiniteArrowItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

	@ModifyVariable(method = "releaseUsing", at = @At("STORE"), index = 10)
	private boolean isInfinite(boolean original, ItemStack stack, Level world, LivingEntity user, @Local(index = 7) ItemStack projectile) {
		if (projectile.getItem() instanceof InfiniteArrowItem infiniteArrowItem)
			return ((Player) user).getAbilities().instabuild || infiniteArrowItem.isInfinite(projectile, stack, (Player) user);
		return original;
	}

	@ModifyVariable(method = "releaseUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
	public AbstractArrow customArrowTest(AbstractArrow oldArrow) {
		Item bowItem = oldArrow.getOwner() instanceof Player owner ? owner.getMainHandItem().getItem() : null;

		if (bowItem != null) {
			if (bowItem instanceof CustomArrowItem arrowItem) {
				return arrowItem.customArrow(oldArrow);
			}
		}

		return oldArrow;
	}
}
