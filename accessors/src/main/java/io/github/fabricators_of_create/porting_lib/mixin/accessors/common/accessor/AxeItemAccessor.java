package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.world.item.AxeItem;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Invoker("getStripped")
	Optional<BlockState> porting_lib$getStripped(BlockState unstrippedState);
}
