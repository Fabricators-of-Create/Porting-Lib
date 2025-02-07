package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StairsBlock.class)
public interface StairBlockAccessor {
	@Invoker("<init>")
	static StairsBlock port_lib$init(BlockState baseBlockState, AbstractBlock.Settings properties) {
		throw new AssertionError("Mixin application failed!");
	}
}
