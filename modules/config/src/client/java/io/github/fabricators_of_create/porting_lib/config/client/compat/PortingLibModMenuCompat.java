package io.github.fabricators_of_create.porting_lib.config.client.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import io.github.fabricators_of_create.porting_lib.config.PortingLibConfig;
import io.github.fabricators_of_create.porting_lib.config.client.gui.ConfigurationScreen;
import net.fabricmc.loader.api.FabricLoader;

public class PortingLibModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ConfigurationScreen(FabricLoader.getInstance().getModContainer(PortingLibConfig.ID).orElseThrow(), parent);
	}
}
