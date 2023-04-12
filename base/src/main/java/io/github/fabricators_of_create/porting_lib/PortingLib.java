package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.command.ModIdArgument;
import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipe;
import io.github.fabricators_of_create.porting_lib.loot.CanToolPerformAction;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class PortingLib implements ModInitializer {

	@Override
	public void onInitialize() {
		TierSortingRegistry.init();
		LootModifierManager.init();
		ConditionalRecipe.init();
		ItemItemStorages.init();
		PortingHooks.init();
		// can be used to force all mixins to apply
		// MixinEnvironment.getCurrentEnvironment().audit();

		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:can_tool_perform_action"), CanToolPerformAction.LOOT_CONDITION_TYPE);

		ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, PortingConstants.id("modid").toString(), ModIdArgument.class,
				SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
	}
}
