package io.github.fabricators_of_create.porting_lib.item.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.SkullBlock;

public class PortingLibItemsTestmod implements ModInitializer {
	public static final ResourceLocation TAB1 = PortingLib.id("tab1");
	public static final ResourceLocation TAB2 = PortingLib.id("tab2");
	public static final Item NO_REPAIR_PICK = new PickaxeItem(Tiers.NETHERITE, new Item.Properties().port_lib$setNoRepair().fireResistant().attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -2.8F)));
	@Override
	public void onInitialize() {
		var tab1 = PortingLibCreativeTab.builder()
				.title(Component.literal("Test tab 1"))
				.withTabsBefore(TAB2)
				.withLabelColor(14525)
				.withSlotColor(2526789)
				.displayItems((itemDisplayParameters, output) -> {
					BuiltInRegistries.ITEM.forEach(item -> {
						if (item instanceof HoeItem)
							output.accept(item);
					});
					output.accept(NO_REPAIR_PICK);
				})
				.build();
		var tab2 = PortingLibCreativeTab.builder()
				.title(Component.literal("Test tab 2"))
				.displayItems((itemDisplayParameters, output) -> {
					BuiltInRegistries.ITEM.forEach(item -> {
						if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SkullBlock)
							output.accept(item);
					});
				})
				.build();

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB1, tab1);
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB2, tab2);
		Registry.register(BuiltInRegistries.ITEM, PortingLib.id("no_repair_pick"), NO_REPAIR_PICK);
	}
}
