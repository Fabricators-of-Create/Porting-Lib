package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public interface BlockBreakResetItem {
	/**
	 * Called when the player is mining a block and the item in their hand changes.
	 * Allows to not reset block breaking if only NBT or similar changes.
	 *
	 * @param oldStack The old stack that was used for mining. Item in players main
	 *                 hand
	 * @param newStack The new stack
	 * @return True to reset block break progress
	 */
	default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		if (!newStack.is(oldStack.getItem()))
			return true;

		if (!newStack.isDamageableItem() || !oldStack.isDamageableItem())
			return !ItemStack.isSameItemSameComponents(newStack, oldStack);

		DataComponentMap newComponents = newStack.getComponents();
		DataComponentMap oldComponents = oldStack.getComponents();

		if (newComponents.isEmpty() || oldComponents.isEmpty())
			return !(newComponents.isEmpty() && oldComponents.isEmpty());

		Set<DataComponentType<?>> newKeys = new HashSet<>(newComponents.keySet());
		Set<DataComponentType<?>> oldKeys = new HashSet<>(oldComponents.keySet());

		newKeys.remove(DataComponents.DAMAGE);
		oldKeys.remove(DataComponents.DAMAGE);

		if (!newKeys.equals(oldKeys))
			return true;

		return !newKeys.stream().allMatch(key -> Objects.equals(newComponents.get(key), oldComponents.get(key)));
	}
}
