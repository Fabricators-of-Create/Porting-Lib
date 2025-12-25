package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.item.extensions.PiglinsNeutralItem;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@WrapOperation(method = "isWearingSafeArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
	private static boolean isNeutralItem(ItemStack instance, TagKey<Item> tag, Operation<Boolean> original, LivingEntity entity) {
		if(instance.getItem() instanceof PiglinsNeutralItem piglinsNeutralItem)
			return piglinsNeutralItem.makesPiglinsNeutral(instance, entity);
		return original.call(instance, tag);
	}
}
