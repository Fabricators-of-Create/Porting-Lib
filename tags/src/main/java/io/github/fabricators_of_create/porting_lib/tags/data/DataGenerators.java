package io.github.fabricators_of_create.porting_lib.tags.data;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class DataGenerators {
	public static void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(FluidTagProvider::new);
		var blockTags = pack.addProvider(BlockTagProvider::new);
		pack.addProvider((output, registriesFuture) -> new ItemTagProvider(output, registriesFuture, blockTags));
		pack.addProvider(BiomeTagsProvider::new);
		pack.addProvider(EntityTagProvider::new);
		pack.addProvider(DataGenerators::itemTags);
	}

	private static ItemTagLangProvider itemTags(FabricDataOutput output) {
		return new ItemTagLangProvider(output, Tags.Items.class);
	}
}
