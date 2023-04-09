package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class SimpleFlowableFluid extends FlowingFluid {
	private final Supplier<? extends Fluid> flowing;
	private final Supplier<? extends Fluid> still;
	@Nullable
	private final Supplier<? extends Item> bucket;
	@Nullable
	private final Supplier<? extends LiquidBlock> block;
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
	public Fluid getSource() {
		return still.get();
	}

	@Override
	protected boolean canConvertToSource() {
		return infinite;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropResources(state, world, pos, blockEntity);
	}

	@Override
	protected int getSlopeFindDistance(LevelReader world) {
		return flowSpeed;
	}

	@Override
	protected int getDropOff(LevelReader worldIn) {
		return levelDecreasePerBlock;
	}

	@Override
	public Item getBucket() {
		return bucket != null ? bucket.get() : Items.AIR;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !isSame(fluid);
	}

	@Override
	public int getTickDelay(LevelReader world) {
		return tickRate;
	}

	@Override
	protected float getExplosionResistance() {
		return blastResistance;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		if (block != null) {
			return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
		}
		return Blocks.AIR.defaultBlockState();
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
	public boolean isSame(Fluid fluid) {
		return fluid == still.get() || fluid == flowing.get();
	}

	public static class Flowing extends SimpleFlowableFluid {
		public Flowing(Properties properties) {
			super(properties);
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Still extends SimpleFlowableFluid {
		public Still(Properties properties) {
			super(properties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Properties {
		private Supplier<? extends Fluid> still;
		private Supplier<? extends Fluid> flowing;
		private FluidAttributes.Builder attributes;
		private boolean infinite;
		private Supplier<? extends Item> bucket;
		private Supplier<? extends LiquidBlock> block;
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

		public Properties block(Supplier<? extends LiquidBlock> block) {
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
