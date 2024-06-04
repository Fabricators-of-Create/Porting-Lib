package io.github.fabricators_of_create.porting_lib.fluids.testmod;

import io.github.fabricators_of_create.porting_lib.fluids.FluidInteractionRegistry;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.Blocks;

public class PortingLibFluidsTestmod implements ModInitializer {
	@Override
	public void onInitialize() {
		FluidInteractionRegistry.addInteraction(PortingLibFluids.WATER_TYPE, new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(Blocks.BAMBOO_BLOCK) && level.getBlockState(relativePos).is(Blocks.MAGMA_BLOCK),
                Blocks.EMERALD_BLOCK.defaultBlockState()
        ));
	}
}
