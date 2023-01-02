package io.github.fabricators_of_create.porting_lib.transfer.cache;

import io.github.fabricators_of_create.porting_lib.extensions.ClientLevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockApiLookup} for fluid storage on the client. can only access API provided through TransferUtil. Null directions allowed.
 * @deprecated see ClientBlockApiCache
 */
@SuppressWarnings("NonExtendableApiUsage")
@Deprecated(forRemoval = true)
public class ClientFluidLookupCache implements BlockApiCache<Storage<FluidVariant>, Direction>, ClientBlockApiCache {
	private final ClientLevel world;
	private final BlockPos pos;
	private boolean blockEntityCacheValid = false;
	private BlockEntity cachedBlockEntity = null;
	private BlockState lastState = null;

	public static BlockApiCache<Storage<FluidVariant>, Direction> get(Level level, BlockPos pos) {
		if (level instanceof ClientLevel c)
			return new ClientFluidLookupCache(c, pos);
		return EmptyFluidLookupCache.INSTANCE;
	}

	public ClientFluidLookupCache(ClientLevel world, BlockPos pos) {
		((ClientLevelExtensions) world).port_lib$registerCache(pos ,this);
		this.world = world;
		this.pos = pos.immutable();
	}

	public void invalidate() {
		blockEntityCacheValid = false;
		cachedBlockEntity = null;
		lastState = null;
	}

	@Nullable
	@Override
	public Storage<FluidVariant> find(@Nullable BlockState state, @Nullable Direction context) {
		// Update block entity cache
		getBlockEntity();

		// Get block state
		if (state == null) {
			if (cachedBlockEntity != null) {
				state = cachedBlockEntity.getBlockState();
			} else {
				state = world.getBlockState(pos);
			}
		}

		if (lastState != state) {
			lastState = state;
		}

		// Query the provider
		if (cachedBlockEntity == null)
			return null;
		return TransferUtil.getFluidStorage(cachedBlockEntity, context);
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
	public BlockApiLookup<Storage<FluidVariant>, Direction> getLookup() {
		return FluidStorage.SIDED;
	}

	@Override
	public ServerLevel getWorld() {
		return null; // why
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}
}
