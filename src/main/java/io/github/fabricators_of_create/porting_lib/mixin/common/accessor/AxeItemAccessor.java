package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Accessor("STRIPPABLES")
	static Map<Block, Block> porting_lib$STRIPPABLES() {
		throw new RuntimeException("mixin failed");
	}

	@Invoker("getStripped")
	Optional<BlockState> porting_lib$getStripped(BlockState unstrippedState);
}
