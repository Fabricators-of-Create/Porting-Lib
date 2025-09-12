package io.github.fabricators_of_create.porting_lib.tags.data;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class PortingLibTagsItemTagLangProvider extends FabricLanguageProvider {
	protected PortingLibTagsItemTagLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
		builder.add(Tags.Items.TOOLS_WRENCH, "Wrenches");
	}
}
