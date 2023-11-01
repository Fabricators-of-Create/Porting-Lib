package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
	// left for drop-in compat
	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		return ItemStack.isSameItemSameTags(first, second);
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	public static ItemStack growCopy(ItemStack stack, int amount) {
		return copyStackWithSize(stack, stack.getCount() + amount);
	}

	/**
	 * giveItemToPlayer without preferred slot
	 */
	public static void giveItemToPlayer(Player player, @Nonnull ItemStack stack) {
		try (Transaction tx = TransferUtil.getTransaction()) {
			PlayerInventoryStorage.of(player).offerOrDrop(ItemVariant.of(stack.getItem()), stack.getCount(), tx);
			tx.commit();
		}
	}

	/**
	 * Inserts the given itemstack into the players inventory.
	 * If the inventory can't hold it, the item will be dropped in the world at the players position.
	 * Different from {@link net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage#offerOrDrop(ItemVariant, long, TransactionContext)} by allowing to insert into a specific slot
	 * and also plays a sound.
	 *
	 * @param player The player to give the item to
	 * @param stack  The itemstack to insert
	 */
	public static void giveItemToPlayer(Player player, @Nonnull ItemStack stack, int preferredSlot) {
		if (stack.isEmpty()) return;

		PlayerInventoryStorage inventory = PlayerInventoryStorage.of(player.getInventory());
		Level world = player.level();

		// try adding it into the inventory
		long remainder = stack.getCount();
		// insert into preferred slot first
		if (preferredSlot >= 0 && preferredSlot < inventory.getSlotCount()) {
			remainder -= TransferUtil.insertItem(inventory.getSlot(preferredSlot), stack);
		}
		// then into the inventory in general
		if (remainder > 0) {
			try (Transaction tx = TransferUtil.getTransaction()) {
				// This doesn't play the item pickup sound but who really cares
				remainder -= inventory.offer(ItemVariant.of(stack), remainder, tx);
				tx.commit();
			}
		}

		// play sound if something got picked up
		if (remainder <= 0 || remainder != stack.getCount()) {
			world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		// drop remaining itemstack into the world
		if (remainder > 0 && !world.isClientSide) {
			ItemEntity entityitem = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), stack.copyWithCount((int) remainder));
			entityitem.setPickUpDelay(40);
			entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

			world.addFreshEntity(entityitem);
		}
	}
}
