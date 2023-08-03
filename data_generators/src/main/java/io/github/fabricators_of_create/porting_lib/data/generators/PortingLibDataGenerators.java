package io.github.fabricators_of_create.porting_lib.data.generators;

import java.util.Map;
import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.data.generators.tools.ToolActionsGenerator;
import io.github.fabricators_of_create.porting_lib.tags.data.DataGenerators;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PortingLibDataGenerators implements DataGeneratorEntrypoint {
	public static final String MOD = "porting_lib.datagen.mod";
	private static final Map<String, Consumer<FabricDataGenerator>> GENERATORS = Map.of(
			"porting_lib_tool_actions", ToolActionsGenerator::onInitializeDataGenerator,
			"porting_lib_tags", DataGenerators::onInitializeDataGenerator
	);

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		GENERATORS.get(generator.getModId()).accept(generator);
	}

	@Override
	public String getEffectiveModId() {
		return System.getProperty(MOD);
	}
}
