package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.command.ModIdArgument;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.server.packs.PackType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import java.nio.file.Files;
import java.nio.file.Path;

public class PortingLib implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Porting Lib Base");
	@Override
	public void onInitialize() {
		TierSortingRegistry.init();
		ConditionalRecipe.init();
		ItemItemStorages.init();
		PortingHooks.init();
		// can be used to force all mixins to apply
		// MixinEnvironment.getCurrentEnvironment().audit();
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LootModifierManager.INSTANCE);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:loot_table_id"), LootTableIdCondition.LOOT_TABLE_ID);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:can_tool_perform_action"), CanToolPerformAction.LOOT_CONDITION_TYPE);

		ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, PortingConstants.id("modid").toString(), ModIdArgument.class,
				SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
	}
}
