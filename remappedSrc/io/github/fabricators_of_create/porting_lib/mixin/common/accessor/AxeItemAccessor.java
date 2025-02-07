package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Invoker("getStripped")
	Optional<BlockState> porting_lib$getStripped(BlockState unstrippedState);
}
