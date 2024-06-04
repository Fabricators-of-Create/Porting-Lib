package io.github.fabricators_of_create.porting_lib.models.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class PortingLibModelsTestmod implements ModInitializer {
	public static final Item DERPY_HELMET = new Item(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.HEAD));
	public static final Item STONE_2 = new Item(new FabricItemSettings());
	public static final Block NOT_GLASS = new Block(FabricBlockSettings.copyOf(Blocks.GLASS));

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("derp_helmet"), DERPY_HELMET);
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("stone_2"), STONE_2);
		Registry.register(BuiltInRegistries.BLOCK, PortingLib.id("not_glass"), NOT_GLASS);
	}
}
