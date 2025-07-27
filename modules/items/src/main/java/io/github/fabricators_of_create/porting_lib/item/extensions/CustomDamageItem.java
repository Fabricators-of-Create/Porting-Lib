package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface CustomDamageItem {
	/**
	 * Reduce the durability of this item by the amount given.
	 * This can be used to e.g. consume power from NBT before durability.
	 *
	 * @param stack    The itemstack to damage
	 * @param amount   The amount to damage
	 * @param entity   The entity damaging the item
	 * @param onBroken The on-broken callback from vanilla
	 * @return The amount of damage to pass to the vanilla logic
	 * @apiNote Try to use {@link FabricItem.Settings#customDamage(CustomDamageHandler)} instead where possible.
	 */
	default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
		return amount;
	}
}
