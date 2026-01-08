package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;

import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.*;

@Mixin(EnchantmentsPredicate.class)
@Implements(@Interface(iface = SingleComponentItemPredicate.class, prefix = "port_lib$"))
public abstract class EnchantmentsPredicateMixin implements SingleComponentItemPredicate<ItemEnchantments> {
	@Shadow
	public abstract boolean matches(ItemEnchantments itemEnchantments);

	@Intrinsic(displace = true)
	public boolean port_lib$matches(DataComponentGetter getter) {
		if (getter instanceof ItemStack stack && stack.getItem() instanceof CustomEnchantingBehaviorItem enchantingBehaviorItem) {
			var lookup = PortingHooks.resolveLookup(Registries.ENCHANTMENT);

			if (lookup != null) {
				return matches(enchantingBehaviorItem.getAllEnchantments(stack, lookup));
			}
		}

		return this.matches(getter);
	}
}
