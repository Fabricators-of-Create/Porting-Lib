package io.github.fabricators_of_create.porting_lib.transfer.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xyz.bluspring.forgecapabilities.capabilities.Capability;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;
import xyz.bluspring.forgecapabilities.capabilities.PortingLibCapabilities;

public class ShulkerItemStackInvWrapper extends ItemStackHandlerContainer implements ICapabilityProvider
{
	@ApiStatus.Internal
	@Nullable
	public static ICapabilityProvider createDefaultProvider(ItemStack itemStack)
	{
		var item = itemStack.getItem();
		if (item == Items.SHULKER_BOX ||
				item == Items.BLACK_SHULKER_BOX ||
				item == Items.BLUE_SHULKER_BOX ||
				item == Items.BROWN_SHULKER_BOX ||
				item == Items.CYAN_SHULKER_BOX ||
				item == Items.GRAY_SHULKER_BOX ||
				item == Items.GREEN_SHULKER_BOX ||
				item == Items.LIGHT_BLUE_SHULKER_BOX ||
				item == Items.LIGHT_GRAY_SHULKER_BOX ||
				item == Items.LIME_SHULKER_BOX ||
				item == Items.MAGENTA_SHULKER_BOX ||
				item == Items.ORANGE_SHULKER_BOX ||
				item == Items.PINK_SHULKER_BOX ||
				item == Items.PURPLE_SHULKER_BOX ||
				item == Items.RED_SHULKER_BOX ||
				item == Items.WHITE_SHULKER_BOX ||
				item == Items.YELLOW_SHULKER_BOX
		)
			return new ShulkerItemStackInvWrapper(itemStack);
		return null;
	}

	private final ItemStack stack;
	private final LazyOptional<SlotExposedStorage> holder = LazyOptional.of(() -> this);

	private CompoundTag cachedTag;
	private NonNullList<ItemStack> itemStacksCache;

	private ShulkerItemStackInvWrapper(ItemStack stack)
	{
		super(27);
		this.stack = stack;
	}

	@Override
	public int getSlots()
	{
		return 27;
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot)
	{
		validateSlotIndex(slot);
		return getItemList().get(slot);
	}

	@Override
	public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return insertItem(slot, resource.toStack((int) maxAmount), false).getCount();
	}

	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		if (!isItemValid(slot, stack))
			return stack;

		validateSlotIndex(slot);

		NonNullList<ItemStack> itemStacks = getItemList();

		ItemStack existing = itemStacks.get(slot);

		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

		if (!existing.isEmpty())
		{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate)
		{
			if (existing.isEmpty())
			{
				itemStacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
			}
			else
			{
				existing.grow(reachedLimit ? limit : stack.getCount());
			}
			setItemList(itemStacks);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
	}

	@Override
	public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return extractItem(slot, (int) maxAmount, false).getCount();
	}

	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		NonNullList<ItemStack> itemStacks = getItemList();
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		ItemStack existing = itemStacks.get(slot);

		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract)
		{
			if (!simulate)
			{
				itemStacks.set(slot, ItemStack.EMPTY);
				setItemList(itemStacks);
				return existing;
			}
			else
			{
				return existing.copy();
			}
		}
		else
		{
			if (!simulate)
			{
				itemStacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
				setItemList(itemStacks);
			}

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	private void validateSlotIndex(int slot)
	{
		if (slot < 0 || slot >= getSlots())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, long amount) {
		return isItemValid(slot, resource.toStack((int) amount));
	}

	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return stack.getItem().canFitInsideContainerItems();
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack)
	{
		validateSlotIndex(slot);
		if (!isItemValid(slot, stack)) throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
		NonNullList<ItemStack> itemStacks = getItemList();
		itemStacks.set(slot, stack);
		setItemList(itemStacks);
	}

	private NonNullList<ItemStack> getItemList()
	{
		CompoundTag rootTag = BlockItem.getBlockEntityData(this.stack);
		if (cachedTag == null || !cachedTag.equals(rootTag))
			itemStacksCache = refreshItemList(rootTag);
		return itemStacksCache;
	}

	private NonNullList<ItemStack> refreshItemList(CompoundTag rootTag)
	{
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(), ItemStack.EMPTY);
		if (rootTag != null && rootTag.contains("Items", CompoundTag.TAG_LIST))
		{
			ContainerHelper.loadAllItems(rootTag, itemStacks);
		}
		cachedTag = rootTag;
		return itemStacks;
	}

	private void setItemList(NonNullList<ItemStack> itemStacks)
	{
		CompoundTag existing = BlockItem.getBlockEntityData(this.stack);
		CompoundTag rootTag = ContainerHelper.saveAllItems(existing == null ? new CompoundTag() : existing, itemStacks);
		BlockItem.setBlockEntityData(this.stack, BlockEntityType.SHULKER_BOX, rootTag);
		cachedTag = rootTag;
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		return PortingLibCapabilities.ITEM_HANDLER.orEmpty(cap, this.holder);
	}
}
