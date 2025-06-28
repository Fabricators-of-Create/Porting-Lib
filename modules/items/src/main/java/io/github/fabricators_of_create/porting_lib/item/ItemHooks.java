package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class ItemHooks {
	/**
	 * Used as the default implementation of {@link io.github.fabricators_of_create.porting_lib.item.extensions.CreatorModIdItem#getCreatorModId}. Call that method instead.
	 */
	@Nullable
	public static String getDefaultCreatorModId(ItemStack itemStack) {
		Item item = itemStack.getItem();
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
		String modId = registryName == null ? null : registryName.getNamespace();
		if ("minecraft".equals(modId)) {
			if (item instanceof EnchantedBookItem) {
				Set<Holder<Enchantment>> enchantments = itemStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet();
				if (enchantments.size() == 1) {
					Holder<Enchantment> enchantmentHolder = enchantments.iterator().next();
					Optional<ResourceKey<Enchantment>> key = enchantmentHolder.unwrapKey();
					if (key.isPresent()) {
						return key.get().location().getNamespace();
					}
				}
			} else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
				PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
				Optional<Holder<Potion>> potionType = potionContents.potion();
				Optional<ResourceKey<Potion>> key = potionType.flatMap(Holder::unwrapKey);
				if (key.isPresent()) {
					return key.get().location().getNamespace();
				}
			} else if (item instanceof SpawnEggItem spawnEggItem) {
				Optional<ResourceKey<EntityType<?>>> key = BuiltInRegistries.ENTITY_TYPE.getResourceKey(spawnEggItem.getType(itemStack));
				if (key.isPresent()) {
					return key.get().location().getNamespace();
				}
			}
		}
		return modId;
	}
}
