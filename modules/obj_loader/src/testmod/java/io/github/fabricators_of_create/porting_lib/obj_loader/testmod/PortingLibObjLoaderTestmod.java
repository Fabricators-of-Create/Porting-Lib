package io.github.fabricators_of_create.porting_lib.obj_loader.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class PortingLibObjLoaderTestmod implements ModInitializer {
	public static final Item RING = new RingItem(new FabricItemSettings());

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("ring"), RING);
	}
}
