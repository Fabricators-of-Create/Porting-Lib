package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.transfer.internal.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.internal.cache.EmptyFluidLookupCache;
import io.github.fabricators_of_create.porting_lib.transfer.internal.cache.EmptyItemLookupCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A simple wrapper around storage lookups that's safe for client-side and uses caching where possible.
 */
public class StorageProvider<T> implements Function<Direction, Storage<T>> {
	public final BlockApiLookup<Storage<T>, Direction> lookup;
	public final Level level;
	public final BlockPos pos;

	protected StorageProvider(BlockApiLookup<Storage<T>, Direction> lookup, Level level, BlockPos pos) {
		this.lookup = lookup;
		this.level = level;
		this.pos = pos;
	}

	@Nullable
	public Storage<T> get(Direction direction) {
		return lookup.find(level, pos, direction);
	}

	@Nullable
	public BlockEntity findBlockEntity() {
		return level.getBlockEntity(pos);
	}

	public BlockState findBlockState() {
		return level.getBlockState(pos);
	}

	/**
	 * Filter this provider, so it will only provide storages that match the given predicate.
	 */
	public StorageProvider<T> filter(BiPredicate<StorageProvider<T>, Storage<T>> filter) {
		return new FilteringStorageProvider<>(this, filter);
	}

	@Override
	@Nullable
	public Storage<T> apply(Direction direction) {
		return get(direction);
	}

	/**
	 * Create a storage provider for {@link FluidStorage#SIDED fluids}.
	 */
	public static StorageProvider<FluidVariant> createForFluids(Level level, BlockPos pos) {
		BlockApiCache<Storage<FluidVariant>, Direction> cache = ((LevelExtensions) level).port_lib$getFluidApiCache(pos);
		if (cache instanceof EmptyFluidLookupCache)
			return create(FluidStorage.SIDED, level, pos);
		return create(cache, level);
	}

	/**
	 * Create a storage provider for {@link ItemStorage#SIDED items}.
	 */
	public static StorageProvider<ItemVariant> createForItems(Level level, BlockPos pos) {
		BlockApiCache<Storage<ItemVariant>, Direction> cache = ((LevelExtensions) level).port_lib$getItemCache(pos);
		if (cache instanceof EmptyItemLookupCache)
			return create(ItemStorage.SIDED, level, pos);
		return create(cache, level);
	}

	/**
	 * Create a storage provider for the given cache in the given level.
	 * The level cannot be retrieved from the cache as they only support server levels.
	 * Prefer {@link #createForItems(Level, BlockPos)} or {@link #createForFluids(Level, BlockPos)}.
	 */
	public static <T> StorageProvider<T> create(BlockApiCache<Storage<T>, Direction> cache, Level level) {
		return new CachedStorageProvider<>(cache, level);
	}

	/**
	 * Create a storage provider for the given lookup. This provider will not have caching.
	 * Prefer {@link #createForItems(Level, BlockPos)} or {@link #createForFluids(Level, BlockPos)}.
	 */
	public static <T> StorageProvider<T> create(BlockApiLookup<Storage<T>, Direction> lookup, Level level, BlockPos pos) {
		return new StorageProvider<>(lookup, level, pos);
	}

	/**
	 * A storage provider that uses {@link BlockApiCache caches} to retrieve storages quickly. Should be preferred.
	 */
	protected static class CachedStorageProvider<T> extends StorageProvider<T> {
		private final BlockApiCache<Storage<T>, Direction> cache;

		public CachedStorageProvider(BlockApiCache<Storage<T>, Direction> cache, Level level) {
			super(cache.getLookup(), level, cache.getPos());
			this.cache = cache;
		}

		@Override
		public Storage<T> get(Direction direction) {
			return cache.find(direction);
		}

		@Override
		@Nullable
		public BlockEntity findBlockEntity() {
			return cache.getBlockEntity();
		}

		@Override
		public BlockState findBlockState() {
			BlockEntity be = findBlockEntity();
			if (be != null)
				return be.getBlockState();
			return super.findBlockState();
		}
	}

	/**
	 * A storage provider that wraps another provider, filtering its storages. It will only provide storages that match its filter.
	 */
	protected static class FilteringStorageProvider<T> extends StorageProvider<T> {
		private final StorageProvider<T> wrapped;
		private final BiPredicate<StorageProvider<T>, Storage<T>> filter;

		protected FilteringStorageProvider(StorageProvider<T> wrapped, BiPredicate<StorageProvider<T>, Storage<T>> filter) {
			super(wrapped.lookup, wrapped.level, wrapped.pos);
			this.wrapped = wrapped;
			this.filter = filter;
		}

		@Override
		@Nullable
		public Storage<T> get(Direction direction) {
			Storage<T> storage = wrapped.get(direction);
			if (filter.test(this, storage)) {
				return storage;
			}
			return null;
		}

		@Override
		@Nullable
		public BlockEntity findBlockEntity() {
			return wrapped.findBlockEntity();
		}

		@Override
		public BlockState findBlockState() {
			return wrapped.findBlockState();
		}
	}
}
