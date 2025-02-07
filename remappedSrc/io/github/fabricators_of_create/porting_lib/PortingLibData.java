package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.tags.ToolTags;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;

public class PortingLibData implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.addProvider(new FabricTagProvider.ItemTagProvider(generator) {
			@Override
			protected void generateTags() {
				getOrCreateTagBuilder(ToolTags.PICKAXES)
						.add(Items.WOODEN_PICKAXE)
						.add(Items.STONE_PICKAXE)
						.add(Items.GOLDEN_PICKAXE)
						.add(Items.IRON_PICKAXE)
						.add(Items.DIAMOND_PICKAXE)
						.add(Items.NETHERITE_PICKAXE);
				getOrCreateTagBuilder(ToolTags.AXES)
						.add(Items.WOODEN_AXE)
						.add(Items.STONE_AXE)
						.add(Items.GOLDEN_AXE)
						.add(Items.IRON_AXE)
						.add(Items.DIAMOND_AXE)
						.add(Items.NETHERITE_AXE);
				getOrCreateTagBuilder(ToolTags.SHOVELS)
						.add(Items.WOODEN_SHOVEL)
						.add(Items.STONE_SHOVEL)
						.add(Items.GOLDEN_SHOVEL)
						.add(Items.IRON_SHOVEL)
						.add(Items.DIAMOND_SHOVEL)
						.add(Items.NETHERITE_SHOVEL);
				getOrCreateTagBuilder(ToolTags.HOES)
						.add(Items.WOODEN_HOE)
						.add(Items.STONE_HOE)
						.add(Items.GOLDEN_HOE)
						.add(Items.IRON_HOE)
						.add(Items.DIAMOND_HOE)
						.add(Items.NETHERITE_HOE);
				getOrCreateTagBuilder(ToolTags.SHEARS)
						.add(Items.SHEARS);
			}
		});
	}
}
