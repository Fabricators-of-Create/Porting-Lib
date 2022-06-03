package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.biome.BiomeDictionary;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipe;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import io.github.fabricators_of_create.porting_lib.util.TrueCondition;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingLib implements ModInitializer {
	public static final String ID = "porting_lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		ServerLifecycleHooks.init();
		PortingLibAttributes.init();
		TierSortingRegistry.init();
		ConditionalRecipe.init();
		ItemItemStorages.init();
		BiomeDictionary.init();
		TransferUtil.initApi();
		CraftingHelper.init();
		TrueCondition.init();
		PortingHooks.init();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
