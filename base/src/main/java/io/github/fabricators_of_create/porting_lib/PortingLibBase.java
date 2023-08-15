package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.util.UsernameCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fabricators_of_create.porting_lib.command.ModIdArgument;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.data.ConditionalRecipe;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;

public class PortingLibBase implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Porting Lib Base");
	@Override
	public void onInitialize() {
		TierSortingRegistry.init();
		ConditionalRecipe.init();
		ItemItemStorages.init();
		UsernameCache.init();
		PortingHooks.init();
		// can be used to force all mixins to apply
		// MixinEnvironment.getCurrentEnvironment().audit();

		ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, PortingLib.id("modid").toString(), ModIdArgument.class,
				SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
	}
}
