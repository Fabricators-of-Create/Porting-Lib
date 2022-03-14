package io.github.fabricators_of_create.porting_lib.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.item.wrapper.PlayerMainInvWrapper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ItemHandlerHelper {
	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		if (first.isEmpty() || !first.sameItem(second) || first.hasTag() != second.hasTag()) return false;

		return !first.hasTag() || first.getTag().equals(second.getTag());
	}

	public static ItemStack copyStackWithSize(ItemStack stack, int size) {
		if (size == 0) return ItemStack.EMPTY;
		ItemStack copy = stack.copy();
		copy.setCount(size);
		return copy;
	}

	public static ItemStack insertItemStacked(IItemHandler inv, ItemStack stack, boolean sim) {
		if (inv == null || stack.isEmpty()) return stack;
		if (!stack.isStackable()) return insertItem(inv, stack, sim);

		int slotCount = inv.getSlots();

		for (int i = 0; i < slotCount; i++) {
			ItemStack stackInSlot = inv.getStackInSlot(i);
			if (canItemStacksStack(stack, stackInSlot)) {
				stack = inv.insertItem(i, stack, sim);
				if (stack.isEmpty()) break;
			}
		}

		if (!stack.isEmpty()) {
			for (int i = 0; i < slotCount; i++) {
				if (inv.getStackInSlot(i).isEmpty()) {
					stack = inv.insertItem(i, stack, sim);
					if (stack.isEmpty()) break;
				}
			}
		}

		return stack;
	}

	/** giveItemToPlayer without preferred slot */
	public static void giveItemToPlayer(Player player, @Nonnull ItemStack stack) {
		giveItemToPlayer(player, stack, -1);
	}

	/**
	 * Inserts the given itemstack into the players inventory.
	 * If the inventory can't hold it, the item will be dropped in the world at the players position.
	 *
	 * @param player The player to give the item to
	 * @param stack  The itemstack to insert
	 */
	public static void giveItemToPlayer(Player player, @Nonnull ItemStack stack, int preferredSlot)
	{
		if (stack.isEmpty()) return;

		IItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
		Level world = player.level;

		// try adding it into the inventory
		ItemStack remainder = stack;
		// insert into preferred slot first
		if (preferredSlot >= 0 && preferredSlot < inventory.getSlots())
		{
			remainder = inventory.insertItem(preferredSlot, stack, false);
		}
		// then into the inventory in general
		if (!remainder.isEmpty())
		{
			remainder = insertItemStacked(inventory, remainder, false);
		}

		// play sound if something got picked up
		if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
		{
			world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		// drop remaining itemstack into the world
		if (!remainder.isEmpty() && !world.isClientSide)
		{
			ItemEntity entityitem = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
			entityitem.setPickUpDelay(40);
			entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

			world.addFreshEntity(entityitem);
		}
	}

	/**
	 * @return stack extracted
	 */
	public static ItemStack extract(IItemHandler inv, ItemStack stack, boolean sim) {
		int toExtract = stack.getCount();
		int totalSlots = inv.getSlots();
		ItemStack finalStack = ItemStack.EMPTY;

		for (int i = 0; i < totalSlots; i++) {
			ItemStack stackInSlot = inv.getStackInSlot(i);
			if (!canItemStacksStack(stackInSlot, stack)) continue;
			ItemStack extracted = inv.extractItem(i, toExtract, sim);
			toExtract -= extracted.getCount();
			if (finalStack == ItemStack.EMPTY) {
				finalStack = extracted;
			} else {
				finalStack.setCount(finalStack.getCount() + extracted.getCount());
			}
		}

		return finalStack;
	}

	/**
	 * @return stack not inserted
	 */
	public static ItemStack insertItem(IItemHandler inv, ItemStack stack, boolean sim) {
		if (inv == null || stack.isEmpty()) return stack;

		for (int i = 0; i < inv.getSlots(); i++) {
			stack = inv.insertItem(i, stack, sim);
			if (stack.isEmpty()) return ItemStack.EMPTY;
		}

		return stack;
	}
}
