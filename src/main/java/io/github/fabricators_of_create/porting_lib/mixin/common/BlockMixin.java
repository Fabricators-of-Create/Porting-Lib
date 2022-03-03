package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.BlockExtensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements RegistryNameProvider, BlockExtensions {

	private BlockMixin(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Shadow
	public abstract SoundType getSoundType(BlockState blockState);

	@Unique
	@Override
	public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
		return getSoundType(state);
	}

	@Unique
	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getLightEmission();
	}

	@Unique
	private ResourceLocation port_lib$registryName = null;

	@Override
	public ResourceLocation getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.BLOCK.getKey((Block) (Object) this);
		}
		return port_lib$registryName;
	}
}
