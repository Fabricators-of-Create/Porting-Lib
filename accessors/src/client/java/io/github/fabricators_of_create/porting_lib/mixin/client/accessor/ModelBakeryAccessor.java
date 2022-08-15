package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.minecraft.client.resources.model.ModelBakery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public interface ModelBakeryAccessor {
	@Invoker("predicate")
	static Predicate<BlockState> port_lib$predicate(StateDefinition<Block, BlockState> container, String variant) {
		throw new RuntimeException("mixin failed!");
	}
}
