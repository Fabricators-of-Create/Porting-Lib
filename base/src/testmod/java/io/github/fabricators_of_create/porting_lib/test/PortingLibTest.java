package io.github.fabricators_of_create.porting_lib.test;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class PortingLibTest implements ModInitializer {
	public static final ChestBlock STRANGE_CHEST = Registry.register(BuiltInRegistries.BLOCK, PortingLib.id("strange_chest"), new StrangeChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()));
	public static final BlockEntityType<StrangeChestBlockEntity> STRANGE_CHEST_BLOCK_ENTITY_TYPE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, PortingLib.id("strange_chest"), FabricBlockEntityTypeBuilder.create(StrangeChestBlockEntity::new, PortingLibTest.STRANGE_CHEST).build());

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("strange_chest"), new BlockItem(STRANGE_CHEST, new Item.Properties()));
	}
}
