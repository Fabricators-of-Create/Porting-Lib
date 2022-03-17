package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.BlockExtensions;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements BlockExtensions, RegistryNameProvider {

	private BlockMixin(BlockBehaviour.Properties properties) {
		super(properties);
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
