package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
	@Accessor("DEFAULT_OVERWORLD")
	static DimensionType port_lib$getDefaultOverworld() {
		throw new AssertionError("Mixin application failed!");
	}
}
