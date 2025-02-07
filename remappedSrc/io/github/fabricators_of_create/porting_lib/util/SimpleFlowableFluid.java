package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class SimpleFlowableFluid extends FlowableFluid {
	private final Supplier<? extends Fluid> flowing;
	private final Supplier<? extends Fluid> still;
	@Nullable
	private final Supplier<? extends Item> bucket;
	@Nullable
	private final Supplier<? extends FluidBlock> block;
	/**
	 * @deprecated Use FluidVariantAttributes
	 */
	@Deprecated(forRemoval = true)
	private final FluidAttributes.Builder builder;
	private final boolean infinite;
	private final int flowSpeed;
	private final int levelDecreasePerBlock;
	private final float blastResistance;
	private final int tickRate;

	protected SimpleFlowableFluid(Properties properties) {
		this.flowing = properties.flowing;
		this.still = properties.still;
		this.builder = properties.attributes;
		this.infinite = properties.infinite;
		this.bucket = properties.bucket;
		this.block = properties.block;
		this.flowSpeed = properties.flowSpeed;
		this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
		this.blastResistance = properties.blastResistance;
		this.tickRate = properties.tickRate;
	}

	@Override
	public Fluid getFlowing() {
		return flowing.get();
	}

	@Override
	public Fluid getStill() {
		return still.get();
	}

	@Override
	protected boolean isInfinite() {
		return infinite;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	@Override
	protected int getFlowSpeed(WorldView world) {
		return flowSpeed;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView worldIn) {
		return levelDecreasePerBlock;
	}

	@Override
	public Item getBucketItem() {
		return bucket != null ? bucket.get() : Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !matchesType(fluid);
	}

	@Override
	public int getTickRate(WorldView world) {
		return tickRate;
	}

	@Override
	protected float getBlastResistance() {
		return blastResistance;
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		if (block != null) {
			return block.get().getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
		}
		return Blocks.AIR.getDefaultState();
	}

	/**
	 * @deprecated Use FluidVariantAttributes
	 */
	@Deprecated(forRemoval = true)
	@Override
	public FluidAttributes createAttributes() {
		return builder.build(this);
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == still.get() || fluid == flowing.get();
	}

	public static class Flowing extends SimpleFlowableFluid {
		public Flowing(Properties properties) {
			super(properties);
			setDefaultState(getStateManager().getDefaultState().with(LEVEL, 7));
		}

		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}

	public static class Still extends SimpleFlowableFluid {
		public Still(Properties properties) {
			super(properties);
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}

	public static class Properties {
		private Supplier<? extends Fluid> still;
		private Supplier<? extends Fluid> flowing;
		private FluidAttributes.Builder attributes;
		private boolean infinite;
		private Supplier<? extends Item> bucket;
		private Supplier<? extends FluidBlock> block;
		private int flowSpeed = 4;
		private int levelDecreasePerBlock = 1;
		private float blastResistance = 1;
		private int tickRate = 5;

		public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, FluidAttributes.Builder attributes) {
			this.still = still;
			this.flowing = flowing;
			this.attributes = attributes;
		}

		public Properties canMultiply() {
			infinite = true;
			return this;
		}

		public Properties bucket(Supplier<? extends Item> bucket) {
			this.bucket = bucket;
			return this;
		}

		public Properties block(Supplier<? extends FluidBlock> block) {
			this.block = block;
			return this;
		}

		public Properties flowSpeed(int flowSpeed) {
			this.flowSpeed = flowSpeed;
			return this;
		}

		public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
			this.levelDecreasePerBlock = levelDecreasePerBlock;
			return this;
		}

		public Properties blastResistance(float blastResistance) {
			this.blastResistance = blastResistance;
			return this;
		}

		public Properties tickRate(int tickRate) {
			this.tickRate = tickRate;
			return this;
		}
	}
}
