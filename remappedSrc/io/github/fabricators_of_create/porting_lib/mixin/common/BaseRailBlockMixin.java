package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.BaseRailBlockExtensions;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractRailBlock.class)
public abstract class BaseRailBlockMixin implements BaseRailBlockExtensions {
	@Shadow
	public abstract Property<RailShape> getShapeProperty();

	@Unique
	@Override
	public RailShape getRailDirection(BlockState state, BlockView world, BlockPos pos, @Nullable AbstractRailBlock cart) {
		return state.get(getShapeProperty());
	}
}
