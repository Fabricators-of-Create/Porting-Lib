package io.github.fabricators_of_create.porting_lib.gametest.infrastructure;

import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

/**
 * An extension to vanilla's {@link GameTestHelper} that provides many more helper methods.
 * To use this, see {@link ExtendedTestFunction}.
 */
public class PortingLibGameTestHelper extends GameTestHelper {
	public static final int TICKS_PER_SECOND = 20;
	public static final int TEN_SECONDS = 10 * TICKS_PER_SECOND;
	public static final int FIFTEEN_SECONDS = 15 * TICKS_PER_SECOND;
	public static final int TWENTY_SECONDS = 20 * TICKS_PER_SECOND;

	public PortingLibGameTestHelper(GameTestInfo test) {
		super(test);
	}

	// blocks

	/**
	 * Flip the direction of any block with the {@link BlockStateProperties#FACING} property.
	 */
	public void flipBlock(BlockPos pos) {
		BlockState original = getBlockState(pos);
		if (!original.hasProperty(BlockStateProperties.FACING))
			fail("FACING property not in block: " + BuiltInRegistries.BLOCK.getId(original.getBlock()));
		Direction facing = original.getValue(BlockStateProperties.FACING);
		BlockState reversed = original.setValue(BlockStateProperties.FACING, facing.getOpposite());
		setBlock(pos, reversed);
	}

	/**
	 * Turn on a lever.
	 */
	public void powerLever(BlockPos pos) {
		assertBlockPresent(Blocks.LEVER, pos);
		if (!getBlockState(pos).getValue(LeverBlock.POWERED)) {
			pullLever(pos);
		}
	}

	/**
	 * Turn off a lever.
	 */
	public void unpowerLever(BlockPos pos) {
		assertBlockPresent(Blocks.LEVER, pos);
		if (getBlockState(pos).getValue(LeverBlock.POWERED)) {
			pullLever(pos);
		}
	}

	// block entities

	/**
	 * Get the block entity of the expected type. If the type does not match, this fails the test.
	 */
	public <T extends BlockEntity> T getBlockEntity(BlockEntityType<T> type, BlockPos pos) {
		BlockEntity be = getBlockEntity(pos);
		BlockEntityType<?> actualType = be == null ? null : be.getType();
		if (actualType != type) {
			String actualId = actualType == null ? "null" : BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(actualType).toString();
			String error = "Expected block entity at pos [%s] with type [%s], got [%s]".formatted(
					pos, BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type), actualId
			);
			fail(error);
		}
		return (T) be;
	}

	// entities

	/**
	 * Spawn an item entity at the given position with no velocity.
	 */
	public ItemEntity spawnItem(BlockPos pos, ItemStack stack) {
		Vec3 spawn = Vec3.atCenterOf(absolutePos(pos));
		ServerLevel level = getLevel();
		ItemEntity item = new ItemEntity(level, spawn.x, spawn.y, spawn.z, stack, 0, 0, 0);
		level.addFreshEntity(item);
		return item;
	}

	/**
	 * Spawn item entities given an item and amount. The amount will be split into multiple entities if
	 * larger than the item's max stack size.
	 */
	public void spawnItems(BlockPos pos, Item item, int amount) {
		while (amount > 0) {
			int toSpawn = Math.min(amount, item.getDefaultMaxStackSize());
			amount -= toSpawn;
			ItemStack stack = new ItemStack(item, toSpawn);
			spawnItem(pos, stack);
		}
	}

	/**
	 * Get the first entity found at the given position.
	 */
	public <T extends Entity> T getFirstEntity(EntityType<T> type, BlockPos pos) {
		List<T> list = getEntitiesBetween(type, pos.north().east().above(), pos.south().west().below());
		if (list.isEmpty())
			fail("No entities at pos: " + pos);
		return list.get(0);
	}

	/**
	 * Get a list of all entities between two positions, inclusive.
	 */
	public <T extends Entity> List<T> getEntitiesBetween(EntityType<T> type, BlockPos pos1, BlockPos pos2) {
		BoundingBox box = BoundingBox.fromCorners(absolutePos(pos1), absolutePos(pos2));
		List<? extends T> entities = getLevel().getEntities(type, e -> box.isInside(e.blockPosition()));
		return (List<T>) entities;
	}


	// storage lookups

	public Storage<FluidVariant> getFluidStorage(BlockPos pos) {
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(getLevel(), absolutePos(pos), null);
		if (storage == null)
			fail("fluid storage not present");
		return storage;
	}

	public Storage<ItemVariant> getItemStorage(BlockPos pos) {
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(getLevel(), absolutePos(pos), null);
		if (storage == null)
			fail("item storage not present");
		return storage;
	}

	// transfer - items

	/**
	 * Get a map of contained items to their amounts. This is not safe for NBT!
	 */
	public Object2LongMap<Item> getItemContent(BlockPos pos) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		Object2LongMap<Item> map = new Object2LongArrayMap<>();
		for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
			Item item = view.getResource().getItem();
			long existing = map.getLong(item);
			map.put(item, existing + view.getAmount());
		}
		return map;
	}

	/**
	 * Get the combined total of all ItemStacks inside the inventory.
	 */
	public long getTotalItems(BlockPos pos) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		long total = 0;
		for (StorageView<ItemVariant> view : storage.nonEmptyViews())
			total += view.getAmount();
		return total;
	}

	/**
	 * Of the provided items, assert that at least one is present in the given inventory.
	 */
	public void assertAnyContained(BlockPos pos, Item... items) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		for (Item item : items) {
			long extracted = StorageUtil.simulateExtract(storage, ItemVariant.of(item), 1, null);
			if (extracted != 0)
				return; // found one
		}
		// did not find any
		fail("No matching items " + Arrays.toString(items) + " found in handler at pos: " + pos);
	}

	/**
	 * Assert that the inventory contains all the provided content.
	 */
	public void assertContentPresent(Object2LongMap<Item> content, BlockPos pos) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		content.forEach((item, expectedAmount) -> {
			long extracted = StorageUtil.simulateExtract(storage, ItemVariant.of(item), expectedAmount, null);
			if (extracted != expectedAmount)
				fail("Storage missing " + item + ", only got " + extracted);
		});
	}

	/**
	 * Assert that all the given inventories hold no items.
	 */
	public void assertContainersEmpty(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			assertContainerEmpty(pos);
		}
	}

	/**
	 * Assert that the given inventory holds no items.
	 */
	@Override
	public void assertContainerEmpty(@NotNull BlockPos pos) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		for (StorageView<ItemVariant> ignored : storage.nonEmptyViews())
			fail("Storage not empty");
	}

	/** @see PortingLibGameTestHelper#assertContainerContains(BlockPos, ItemStack) */
	public void assertContainerContains(BlockPos pos, ItemLike item) {
		assertContainerContains(pos, item.asItem());
	}

	/** @see PortingLibGameTestHelper#assertContainerContains(BlockPos, ItemStack) */
	@Override
	public void assertContainerContains(@NotNull BlockPos pos, @NotNull Item item) {
		assertContainerContains(pos, new ItemStack(item));
	}

	/**
	 * Assert that the inventory holds at least the given ItemStack. It may also hold more than the stack.
	 */
	public void assertContainerContains(BlockPos pos, ItemStack item) {
		Storage<ItemVariant> storage = getItemStorage(pos);
		int count = item.getCount();
		long extracted = StorageUtil.simulateExtract(storage, ItemVariant.of(item), count, null);
		if (extracted != count)
			fail("expected " + item + ", got " + extracted);
	}

	// time

	/**
	 * Fail unless the desired number seconds have passed since test start.
	 */
	public void assertSecondsPassed(int seconds) {
		if (getTick() < (long) seconds * TICKS_PER_SECOND)
			fail("Waiting for %s seconds to pass".formatted(seconds));
	}

	/**
	 * Get the total number of seconds that have passed since test start.
	 */
	public long secondsPassed() {
		return getTick() % 20;
	}

	/**
	 * Run an action later, once enough time has passed.
	 */
	public void whenSecondsPassed(int seconds, Runnable run) {
		runAfterDelay((long) seconds * TICKS_PER_SECOND, run);
	}

	// numbers

	/**
	 * Assert that a number is <1 away from its expected value
	 */
	public void assertCloseEnoughTo(double value, double expected) {
		assertInRange(value, expected - 1, expected + 1);
	}

	public void assertInRange(double value, double min, double max) {
		if (value < min)
			fail("Value %s below expected min of %s".formatted(value, min));
		if (value > max)
			fail("Value %s greater than expected max of %s".formatted(value, max));
	}

	@Contract("_->fail") // make IDEA happier
	@Override
	public void fail(@NotNull String exceptionMessage) {
		super.fail(exceptionMessage);
	}
}
