package io.github.fabricators_of_create.porting_lib.models.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public class PortingLibModelsTestmod implements ModInitializer {
	public static Item DERPY_HELMET = new Item(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.HEAD));

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("derp_helmet"), DERPY_HELMET);
	}
}
