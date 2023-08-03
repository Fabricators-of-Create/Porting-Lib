package io.github.fabricators_of_create.porting_lib.data.generators.tools;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ToolActionsGenerator {
	public static void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ToolActionsLootTableProvider::new);
	}
}
