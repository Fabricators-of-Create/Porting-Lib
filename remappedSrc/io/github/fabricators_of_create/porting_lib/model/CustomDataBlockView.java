package io.github.fabricators_of_create.porting_lib.model;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;
import org.jetbrains.annotations.Nullable;

public class CustomDataBlockView implements RenderAttachedBlockView {

	private final RenderAttachedBlockView wrapped;
	private final Object customData;

	public CustomDataBlockView(RenderAttachedBlockView wrapped, Object data) {
		this.wrapped = wrapped;
		this.customData = data;
	}

	@Override
	public float getBrightness(Direction direction, boolean shade) {
		return wrapped.getBrightness(direction, shade);
	}

	@Override
	public LightingProvider getLightingProvider() {
		return wrapped.getLightingProvider();
	}

	@Override
	public int getColor(BlockPos blockPos, ColorResolver colorResolver) {
		return wrapped.getColor(blockPos, colorResolver);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return wrapped.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return wrapped.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return wrapped.getFluidState(pos);
	}

	@Override
	public int getHeight() {
		return wrapped.getHeight();
	}

	@Override
	public int getBottomY() {
		return wrapped.getBottomY();
	}

	@Override
	public @Nullable Object getBlockEntityRenderAttachment(BlockPos pos) {
		return customData;
	}
}
