package io.github.fabricators_of_create.porting_lib.config.client;

import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

public class PortingLibClientConfig {
	static final ModConfigSpec clientSpec;
	public static final Client CLIENT;
	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	/**
	 * Client specific configuration - only loaded clientside from porting_lib_config-client.toml
	 */
	public static class Client {

		public final ModConfigSpec.BooleanValue logUntranslatedConfigurationWarnings;

		Client(ModConfigSpec.Builder builder) {
			logUntranslatedConfigurationWarnings = builder
					.comment("A config option mainly for developers. Logs out configuration values that do not have translations when running a client in a development environment.")
					.translation("porting_lib_config.configgui.logUntranslatedConfigurationWarnings")
					.define("logUntranslatedConfigurationWarnings", true);
		}
	}
}
