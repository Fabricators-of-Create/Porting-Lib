package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import javax.annotation.Nullable;

public interface ItemExtensions {
	default boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

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
	 * Called when the player Left Clicks (attacks) an entity. Processed before
	 * damage is done, if return value is true further processing is canceled and
	 * the entity is not attacked.
	 *
	 * @param stack  The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	default boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
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
	 *         associated mod and {@link RegistryNameProvider#getRegistryName()} would return null.
	 */
	@Nullable
	default String getCreatorModId(ItemStack itemStack) {
		Item item = itemStack.getItem();
		ResourceLocation registryName = item.getRegistryName();
		String modId = registryName == null ? null : registryName.getNamespace();
		if ("minecraft".equals(modId)) {
			if (item instanceof EnchantedBookItem) {
				ListTag enchantmentsNbt = EnchantedBookItem.getEnchantments(itemStack);
				if (enchantmentsNbt.size() == 1) {
					CompoundTag nbttagcompound = enchantmentsNbt.getCompound(0);
					ResourceLocation resourceLocation = ResourceLocation.tryParse(nbttagcompound.getString("id"));
					if (resourceLocation != null && Registry.ENCHANTMENT.containsKey(resourceLocation))
					{
						return resourceLocation.getNamespace();
					}
				}
			}
			else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
				Potion potionType = PotionUtils.getPotion(itemStack);
				ResourceLocation resourceLocation = Registry.POTION.getKey(potionType);
				if (resourceLocation != null) {
					return resourceLocation.getNamespace();
				}
			}
			else if (item instanceof SpawnEggItem) {
				ResourceLocation resourceLocation = ((RegistryNameProvider)((SpawnEggItem)item).getType(null)).getRegistryName();
				if (resourceLocation != null) {
					return resourceLocation.getNamespace();
				}
			}
		}
		return modId;
	}
}
