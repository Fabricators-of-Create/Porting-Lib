package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BaseRailBlockExtensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

@Mixin(BaseRailBlock.class)
public abstract class BaseRailBlockMixin implements BaseRailBlockExtensions {
	@Shadow
	public abstract Property<RailShape> getShapeProperty();

	@Unique
	@Override
	public RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable BaseRailBlock cart) {
		return state.getValue(getShapeProperty());
	}
}
