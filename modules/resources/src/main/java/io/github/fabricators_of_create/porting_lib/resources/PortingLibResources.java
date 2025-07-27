package io.github.fabricators_of_create.porting_lib.resources;

import io.github.fabricators_of_create.porting_lib.resources.conditions.PortingLibConditions;
import io.github.fabricators_of_create.porting_lib.resources.crafting.PortingLibIngredients;
import io.github.fabricators_of_create.porting_lib.resources.data_maps.PortingLibDataMaps;

public class PortingLibResources {
	public static void init() {
		PortingLibConditions.init();
		PortingLibIngredients.init();
		PortingLibDataMaps.init();
	}
}
