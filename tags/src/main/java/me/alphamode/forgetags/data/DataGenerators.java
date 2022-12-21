package me.alphamode.forgetags.data;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(FluidTagProvider::new);
		pack.addProvider(BlockTagProvider::new);
		pack.addProvider(ItemTagProvider::new);
		pack.addProvider(BiomeTagsProvider::new);
		pack.addProvider(EntityTagProvider::new);
		pack.addProvider(DataGenerators::itemTags);
	}

	private static ItemTagLangProvider itemTags(FabricDataOutput output) {
		return new ItemTagLangProvider(output, Tags.Items.class);
	}
}
