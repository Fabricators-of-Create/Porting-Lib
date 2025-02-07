package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.state.StateManager;

@Mixin(ModelLoader.class)
public interface ModelBakeryAccessor {
	@Invoker("predicate")
	static Predicate<BlockState> port_lib$predicate(StateManager<Block, BlockState> container, String variant) {
		throw new RuntimeException("mixin failed!");
	}
}
