package io.github.fabricators_of_create.porting_lib.model;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class CustomDataBlockView implements RenderAttachedBlockView {

	private final RenderAttachedBlockView wrapped;
	private final Object customData;

	public CustomDataBlockView(RenderAttachedBlockView wrapped, Object data) {
		this.wrapped = wrapped;
		this.customData = data;
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return wrapped.getShade(direction, shade);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return wrapped.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return wrapped.getBlockTint(blockPos, colorResolver);
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
	public int getMinBuildHeight() {
		return wrapped.getMinBuildHeight();
	}

	@Override
	public Object getBlockEntityRenderAttachment(BlockPos pos) {
		if (customData == null)
			return ModelData.EMPTY;
		return customData;
	}
}
