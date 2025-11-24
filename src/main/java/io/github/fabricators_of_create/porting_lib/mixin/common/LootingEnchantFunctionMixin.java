package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;

import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootingEnchantFunction.class)
public class LootingEnchantFunctionMixin {
	@WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getMobLooting(Lnet/minecraft/world/entity/LivingEntity;)I"))
	private int port_lib$modifyLootingLevel(LivingEntity killer, Operation<Integer> original, ItemStack stack, LootContext context) {
		int level = original.call(killer);
		Entity target = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (target instanceof LivingEntity living)
			return LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(context.getParamOrNull(LootContextParams.DAMAGE_SOURCE), living, level, false); // Idk whats up with recentlyHit thats not there on forge...
		return level;
	}
}
