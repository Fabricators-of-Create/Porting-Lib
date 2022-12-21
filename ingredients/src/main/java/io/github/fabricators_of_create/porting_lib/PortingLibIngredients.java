package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.api.ModInitializer;

public class PortingLibIngredients implements ModInitializer {
	@Override
	public void onInitialize() {
		CraftingHelper.init();
	}
}
