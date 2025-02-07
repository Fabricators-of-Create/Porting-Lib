package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
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
	default boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
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
		Identifier registryName = item.getRegistryName();
		String modId = registryName == null ? null : registryName.getNamespace();
		if ("minecraft".equals(modId)) {
			if (item instanceof EnchantedBookItem) {
				NbtList enchantmentsNbt = EnchantedBookItem.getEnchantmentNbt(itemStack);
				if (enchantmentsNbt.size() == 1) {
					NbtCompound nbttagcompound = enchantmentsNbt.getCompound(0);
					Identifier resourceLocation = Identifier.tryParse(nbttagcompound.getString("id"));
					if (resourceLocation != null && Registry.ENCHANTMENT.containsId(resourceLocation))
					{
						return resourceLocation.getNamespace();
					}
				}
			}
			else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
				Potion potionType = PotionUtil.getPotion(itemStack);
				Identifier resourceLocation = Registry.POTION.getId(potionType);
				if (resourceLocation != null) {
					return resourceLocation.getNamespace();
				}
			}
			else if (item instanceof SpawnEggItem) {
				Identifier resourceLocation = ((RegistryNameProvider)((SpawnEggItem)item).getEntityType(null)).getRegistryName();
				if (resourceLocation != null) {
					return resourceLocation.getNamespace();
				}
			}
		}
		return modId;
	}

	/**
	 * Determines if this Item has a special entity for when they are in the world.
	 * Is called when a EntityItem is spawned in the world, if true and
	 * Item#createCustomEntity returns non null, the EntityItem will be destroyed
	 * and the new Entity will be added to the world.
	 *
	 * @param stack The current item stack
	 * @return True of the item has a custom entity, If true,
	 *         Item#createCustomEntity will be called
	 */
	default boolean hasCustomEntity(ItemStack stack) {
		return false;
	}

	/**
	 * This function should return a new entity to replace the dropped item.
	 * Returning null here will not kill the EntityItem and will leave it to
	 * function normally. Called when the item it placed in a level.
	 *
	 * @param level     The level object
	 * @param location  The EntityItem object, useful for getting the position of
	 *                  the entity
	 * @param stack The current item stack
	 * @return A new Entity object to spawn or null
	 */
	@Nullable
	default Entity createEntity(World level, Entity location, ItemStack stack) {
		return null;
	}

	/**
	 * Get the tooltip parts that should be hidden by default on the given stack if the {@code HideFlags} tag is not set.
	 * @see ItemStack.TooltipSection
	 * @param stack the stack
	 * @return the default hide flags
	 */
	default int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
		return 0;
	}
}
