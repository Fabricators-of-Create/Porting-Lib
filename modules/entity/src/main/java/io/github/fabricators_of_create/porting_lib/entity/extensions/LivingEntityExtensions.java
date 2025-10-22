package io.github.fabricators_of_create.porting_lib.entity.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import net.minecraft.world.entity.LivingEntity;

import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;

public interface LivingEntityExtensions {
	default LivingEntity self() {
		return (LivingEntity) this;
	}

	/**
	 * Returns whether the entity can drown in the fluid.
	 *
	 * @param type the type of the fluid
	 * @return {@code true} if the entity can drown in the fluid, {@code false} otherwise
	 */
	default boolean canDrownInFluidType(FluidType type) {
		if (type == PortingLibFluids.WATER_TYPE) return !self().canBreatheUnderwater();
		return type.canDrownIn(self());
	}
}

