package me.alphamode.forgetags.data;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(FluidTagProvider::new);
		BlockTagProvider blockTagProvider = fabricDataGenerator.addProvider(BlockTagProvider::new);
		fabricDataGenerator.addProvider(new ItemTagProvider(fabricDataGenerator, blockTagProvider));
		fabricDataGenerator.addProvider(BiomeTagsProvider::new);
		fabricDataGenerator.addProvider(EntityTagProvider::new);

		fabricDataGenerator.addProvider(new ItemTagLangProvider(fabricDataGenerator, Tags.Items.class));
	}
}
