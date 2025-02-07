package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition;
import io.github.fabricators_of_create.porting_lib.util.CanToolPerformAction;
import io.github.fabricators_of_create.porting_lib.util.UsernameCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PortingLib implements ModInitializer {
	public static final String ID = "porting_lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		ServerLifecycleHooks.init();
		PortingLibAttributes.init();
		TierSortingRegistry.init();
		LootModifierManager.init();
		ConditionalRecipe.init();
		ItemItemStorages.init();
		BiomeDictionary.init();
		TransferUtil.initApi();
		CraftingHelper.init();
		TrueCondition.init();
		UsernameCache.load();
		PortingHooks.init();

		Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier("forge:loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);
		Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier("forge:can_tool_perform_action"), CanToolPerformAction.LOOT_CONDITION_TYPE);
	}

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}
}
