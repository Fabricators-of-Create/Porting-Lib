package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.addons.ItemAbilityItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyExpressionValue(method = "hurtCurrentlyUsedShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
	private boolean shieldToolAction(boolean original) {
		if (this.useItem.getItem() instanceof ItemAbilityItem) {
			return this.useItem.canPerformAction(ItemAbilities.SHIELD_BLOCK);
		}
		return original;
	}

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
	private ItemStack canSwordSweep(ItemStack instance) {
		if (instance.getItem() instanceof ItemAbilityItem) {
			if (instance.canPerformAction(ItemAbilities.SWORD_SWEEP)) {
				return instance.getItem() instanceof SwordItem ? instance : Items.IRON_SWORD.getDefaultInstance();
			}
			return Items.AIR.getDefaultInstance();
		}
		return instance;
	}
}
