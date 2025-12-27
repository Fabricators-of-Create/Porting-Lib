package io.github.fabricators_of_create.porting_lib.milk.client.data;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.data.client.ClientExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.milk.data.MilkTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PortingLibMilkData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		ExistingFileHelper helper = ClientExistingFileHelper.withResources();
		pack.addProvider((output, registriesFuture) -> new MilkSoundDefinitionsProvider(output, helper));
		pack.addProvider(MilkTagProvider::new);
	}
}
