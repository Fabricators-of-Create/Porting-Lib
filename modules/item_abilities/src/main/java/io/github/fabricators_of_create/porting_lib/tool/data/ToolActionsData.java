package io.github.fabricators_of_create.porting_lib.tool.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ToolActionsData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		var pack = dataGenerator.createPack();
		pack.addProvider(ToolActionsLootTableProvider::new);
	}
}
