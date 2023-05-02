package io.github.fabricators_of_create.porting_lib.gametest;

import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelectorItem;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.QuickExportCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class PortingLibGameTest implements ModInitializer {

	public static final Item AREA_SELECTOR = new AreaSelectorItem(new FabricItemSettings());

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("porting_lib", "area_selector"), AREA_SELECTOR);
		QuickExportCommand.register();
	}
}
