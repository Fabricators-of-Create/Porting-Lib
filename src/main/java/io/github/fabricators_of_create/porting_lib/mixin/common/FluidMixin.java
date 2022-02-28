package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.FluidExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidAttributes;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public abstract class FluidMixin implements FluidExtensions {
  @Unique
  private FluidAttributes port_lib$fluidAttributes;

  @Unique
  @Override
  public final FluidAttributes getAttributes() {
    if (port_lib$fluidAttributes == null)
		port_lib$fluidAttributes = createAttributes();
    return port_lib$fluidAttributes;
  }
}
