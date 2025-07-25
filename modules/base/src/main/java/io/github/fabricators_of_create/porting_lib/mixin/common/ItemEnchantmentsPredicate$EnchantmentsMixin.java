package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ItemEnchantmentsPredicate.Enchantments.class)
public abstract class ItemEnchantmentsPredicate$EnchantmentsMixin extends ItemEnchantmentsPredicate {
	protected ItemEnchantmentsPredicate$EnchantmentsMixin(List<EnchantmentPredicate> enchantments) {
		super(enchantments);
	}

	@Intrinsic(displace = true)
	public boolean port_lib$matches(ItemStack stack) {
		if (stack.getItem() instanceof CustomEnchantingBehaviorItem enchantingBehaviorItem) {
			var lookup = PortingHooks.resolveLookup(Registries.ENCHANTMENT);

			if (lookup != null) {
				return matches(stack, enchantingBehaviorItem.getAllEnchantments(stack, lookup));
			}
		}

		return super.matches(stack);
	}
}
