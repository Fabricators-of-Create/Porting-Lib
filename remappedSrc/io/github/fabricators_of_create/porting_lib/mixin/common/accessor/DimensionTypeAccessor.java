package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
	@Accessor("DEFAULT_OVERWORLD")
	static DimensionType port_lib$getDefaultOverworld() {
		throw new AssertionError("Mixin application failed!");
	}
}
