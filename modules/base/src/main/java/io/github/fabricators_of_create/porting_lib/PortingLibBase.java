package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.command.ConfigCommand;
import io.github.fabricators_of_create.porting_lib.command.EnumArgument;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import io.github.fabricators_of_create.porting_lib.util.DeferredSpawnEggItem;
import io.github.fabricators_of_create.porting_lib.util.UsernameCache;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fabricators_of_create.porting_lib.command.ModIdArgument;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemItemStorages;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public class PortingLibBase implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Porting Lib Base");
	@Override
	public void onInitialize() {
		ItemItemStorages.init();
		UsernameCache.load();
		// can be used to force all mixins to apply
		// MixinEnvironment.getCurrentEnvironment().audit();

		ArgumentTypeRegistry.registerArgumentType(PortingLib.id("modid"), ModIdArgument.class,
				SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
		ArgumentTypeRegistry.registerArgumentType(PortingLib.id("enum"), EnumArgument.class,
				new EnumArgument.Info());

		CommandRegistrationCallback.EVENT.register(ConfigCommand::register);

		ModsLoadedCallback.EVENT.register(envType -> DeferredSpawnEggItem.init());
	}
}
