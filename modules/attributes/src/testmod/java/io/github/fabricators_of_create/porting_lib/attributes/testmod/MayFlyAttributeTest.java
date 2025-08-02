package io.github.fabricators_of_create.porting_lib.attributes.testmod;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class MayFlyAttributeTest {
	protected static final String MODID = "may_fly_attribute_item";

	private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(MODID, "add_flight");
	private static final AttributeModifier MODIFIER = new AttributeModifier(MODIFIER_ID, 1D, AttributeModifier.Operation.ADD_VALUE);

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
			entries.accept(GOOD);
		});
	}

	/** Successful "scope item" using the Forge method, all cases of stopping using the item will remove the flight ability */
	public static Item GOOD = Registry.register(BuiltInRegistries.ITEM, "good_scope", new InvertedTelescope(new Item.Properties()));

	private static class InvertedTelescope extends Item {
		public InvertedTelescope(Properties props) {
			super(props);
		}

		@Override
		public ItemAttributeModifiers getDefaultAttributeModifiers() {
			return ItemAttributeModifiers.builder()
					.add(PortingLibAttributes.CREATIVE_FLIGHT, MODIFIER, EquipmentSlotGroup.ANY)
					.build();
		}
	}
}
