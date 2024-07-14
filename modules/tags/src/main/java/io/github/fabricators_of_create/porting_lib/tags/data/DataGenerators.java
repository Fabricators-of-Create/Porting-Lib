package io.github.fabricators_of_create.porting_lib.tags.data;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(FluidTagProvider::new);
		BlockTagProvider blocks = pack.addProvider(BlockTagProvider::new);
		pack.addProvider((output, registries) -> new ItemTagProvider(output, registries, blocks));
		pack.addProvider(BiomeTagsProvider::new);
		pack.addProvider(EntityTagProvider::new);
		pack.addProvider(DataGenerators::itemTagLang);
	}

	private static ItemTagLangProvider itemTagLang(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		return new ItemTagLangProvider(output, registryLookup, Tags.Items.class);
	}
}
