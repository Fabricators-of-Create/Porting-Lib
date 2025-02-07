package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.CustomArrowItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
	@ModifyVariable(method = "releaseUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
	public PersistentProjectileEntity customArrowTest(PersistentProjectileEntity oldArrow) {
		Item bowItem = oldArrow.getOwner() instanceof PlayerEntity owner ? owner.getMainHandStack().getItem() : null;

		if(bowItem != null) {
			if(bowItem instanceof CustomArrowItem arrowItem){
				return arrowItem.customArrow(oldArrow);
			}
		}

		return oldArrow;
	}
}
