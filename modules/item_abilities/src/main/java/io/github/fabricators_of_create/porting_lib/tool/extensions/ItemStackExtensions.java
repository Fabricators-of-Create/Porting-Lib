package io.github.fabricators_of_create.porting_lib.tool.extensions;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;

public interface ItemStackExtensions {
	/**
	 * Queries if an item can perform the given action.
	 * See {@link ItemAbilities} for a description of each stock action
	 *
	 * @param itemAbility The action being queried
	 * @return True if the stack can perform the action
	 */
	default boolean canPerformAction(ItemAbility itemAbility) {
		return self().getItem().canPerformAction(self(), itemAbility);
	}
}
