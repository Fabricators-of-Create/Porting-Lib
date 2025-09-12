package io.github.fabricators_of_create.porting_lib.tags.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PortingLibTagsData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		var pack = generator.createPack();
		pack.addProvider(PortingLibTagsItemProvider::new);
		pack.addProvider(PortingLibTagsItemTagLangProvider::new);
		pack.addProvider(PortingLibTagsDamageTypeTagsProvider::new);
	}
}
