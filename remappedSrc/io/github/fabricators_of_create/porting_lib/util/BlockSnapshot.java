package io.github.fabricators_of_create.porting_lib.util;

import java.lang.ref.WeakReference;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a captured snapshot of a block which will not change
 * automatically.
 * <p>
 * Unlike Block, which only one object can exist per coordinate, BlockSnapshot
 * can exist multiple times for any given Block.
 */
public class BlockSnapshot {
	private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));

	private final RegistryKey<World> dim;
	private final BlockPos pos;
	private final int flags;
	private final BlockState block;
	@Nullable
	private final NbtCompound nbt;

	@Nullable
	private WeakReference<WorldAccess> level;
	private String toString = null;

	private BlockSnapshot(RegistryKey<World> dim, WorldAccess level, BlockPos pos, BlockState state, @Nullable NbtCompound nbt, int flags) {
		this.dim = dim;
		this.pos = pos.toImmutable();
		this.block = state;
		this.flags = flags;
		this.nbt = nbt;

		this.level = new WeakReference<>(level);

		if (DEBUG)
			System.out.println("Created " + this.toString());
	}

	public static BlockSnapshot create(RegistryKey<World> dim, WorldAccess world, BlockPos pos) {
		return create(dim, world, pos, 3);
	}

	public static BlockSnapshot create(RegistryKey<World> dim, WorldAccess world, BlockPos pos, int flag) {
		return new BlockSnapshot(dim, world, pos, world.getBlockState(pos), getBlockEntityTag(world.getBlockEntity(pos)), flag);
	}

	@Nullable
	private static NbtCompound getBlockEntityTag(@Nullable BlockEntity te) {
		return te == null ? null : te.saveWithFullMetadata();
	}

	public BlockState getCurrentBlock() {
		WorldAccess world = getLevel();
		return world == null ? Blocks.AIR.getDefaultState() : world.getBlockState(this.pos);
	}

	@Nullable
	public WorldAccess getLevel() {
		WorldAccess world = this.level != null ? this.level.get() : null;
		if (world == null) {
			world = ServerLifecycleHooks.getCurrentServer().getWorld(this.dim);
			this.level = new WeakReference<WorldAccess>(world);
		}
		return world;
	}

	public BlockState getReplacedBlock() {
		return this.block;
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		return getTag() != null ? BlockEntity.createFromNbt(getPos(), getReplacedBlock(), getTag()) : null;
	}

	public boolean restore() {
		return restore(false);
	}

	public boolean restore(boolean force)
	{
		return restore(force, true);
	}

	public boolean restore(boolean force, boolean notifyNeighbors) {
		return restoreToLocation(getLevel(), getPos(), force, notifyNeighbors);
	}

	public boolean restoreToLocation(WorldAccess world, BlockPos pos, boolean force, boolean notifyNeighbors) {
		BlockState current = getCurrentBlock();
		BlockState replaced = getReplacedBlock();

		int flags = notifyNeighbors ? Block.NOTIFY_ALL : Block.NOTIFY_LISTENERS;

		if (current != replaced) {
			if (force)
				world.setBlockState(pos, replaced, flags);
			else
				return false;
		}

		world.setBlockState(pos, replaced, flags);
		if (world instanceof World)
			((World)world).updateListeners(pos, current, replaced, flags);

		BlockEntity te = null;
		if (getTag() != null) {
			te = world.getBlockEntity(pos);
			if (te != null) {
				te.readNbt(getTag());
				te.markDirty();
			}
		}

		if (DEBUG)
			System.out.println("Restored " + this.toString());
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		final BlockSnapshot other = (BlockSnapshot) obj;
		return this.dim.equals(other.dim) &&
				this.pos.equals(other.pos) &&
				this.block == other.block &&
				this.flags == other.flags &&
				Objects.equals(this.nbt, other.nbt);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 73 * hash + this.dim.hashCode();
		hash = 73 * hash + this.pos.hashCode();
		hash = 73 * hash + this.block.hashCode();
		hash = 73 * hash + this.flags;
		hash = 73 * hash + Objects.hashCode(this.getTag());
		return hash;
	}

	@Override
	public String toString() {
		if (toString == null) {
			this.toString =
					"BlockSnapshot[" +
							"World:" + this.dim.getValue() + ',' +
							"Pos: " + this.pos + ',' +
							"State: " + this.block + ',' +
							"Flags: " + this.flags + ',' +
							"NBT: " + (this.nbt == null ? "null" : this.nbt.toString()) +
							']';
		}
		return this.toString;
	}

	public BlockPos getPos() { return pos; }


	public int getFlag() { return flags; }

	@Nullable
	public NbtCompound getTag() { return nbt; }

}
