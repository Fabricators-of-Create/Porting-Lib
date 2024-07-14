package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
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

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface ItemExtensions {
	/**
	 * Called before a block is broken. Return true to prevent default block
	 * harvesting.
	 *
	 * Note: In SMP, this is called on both client and server sides!
	 *
	 * @param itemstack The current ItemStack
	 * @param pos       Block's position in world
	 * @param player    The Player that is wielding the item
	 * @return True to prevent harvesting, false to continue as normal
	 */
	default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		return false;
	}

	/**
	 * Called to get the Mod ID of the mod that *created* the ItemStack, instead of
	 * the real Mod ID that *registered* it.
	 *
	 * For example the Forge Universal Bucket creates a subitem for each modded
	 * fluid, and it returns the modded fluid's Mod ID here.
	 *
	 * Mods that register subitems for other mods can override this. Informational
	 * mods can call it to show the mod that created the item.
	 *
	 * @param itemStack the ItemStack to check
	 * @return the Mod ID for the ItemStack, or null when there is no specially
	 *         associated mod and {@link Registry#getKey(Object)} would return null.
	 */
	@Nullable
	default String getCreatorModId(ItemStack itemStack) {
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
