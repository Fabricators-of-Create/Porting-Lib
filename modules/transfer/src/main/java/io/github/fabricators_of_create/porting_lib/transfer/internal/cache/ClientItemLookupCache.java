package io.github.fabricators_of_create.porting_lib.transfer.internal.cache;

import io.github.fabricators_of_create.porting_lib.transfer.internal.extensions.ClientLevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.StorageProvider;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

/**
 * This should not be used directly. Use {@link StorageProvider} instead.
 */
@Internal
@SuppressWarnings("NonExtendableApiUsage")
public class ClientItemLookupCache implements BlockApiCache<Storage<ItemVariant>, Direction>, ClientBlockApiCache {
	private final ClientLevel world;
	private final BlockPos pos;
	private boolean blockEntityCacheValid = false;
	private BlockEntity cachedBlockEntity = null;

	public static BlockApiCache<Storage<ItemVariant>, Direction> get(Level level, BlockPos pos) {
		if (level instanceof ClientLevel c)
			return new ClientItemLookupCache(c, pos);
		return new EmptyItemLookupCache(pos);
	}

	public ClientItemLookupCache(ClientLevel world, BlockPos pos) {
		((ClientLevelExtensions) world).port_lib$registerCache(pos ,this);
		this.world = world;
		this.pos = pos.immutable();
	}

	public void invalidate() {
		blockEntityCacheValid = false;
		cachedBlockEntity = null;
	}

	@Nullable
	@Override
	public Storage<ItemVariant> find(@Nullable BlockState state, Direction context) {
		// Update block entity cache
		getBlockEntity();
		// Query the provider
		if (cachedBlockEntity == null)
			return null;
		return TransferUtil.getItemStorage(world, pos, cachedBlockEntity, context);
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity() {
		if (!blockEntityCacheValid) {
			cachedBlockEntity = world.getBlockEntity(pos);
			blockEntityCacheValid = true;
		}

		return cachedBlockEntity;
	}

	@Override
	public BlockApiLookup<Storage<ItemVariant>, Direction> getLookup() {
		return ItemStorage.SIDED;
	}

	@Override
	public ServerLevel getWorld() {
		throw new UnsupportedOperationException("Cannot call getWorld on a client-side cache as only ServerLevels are supported");
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}
}
