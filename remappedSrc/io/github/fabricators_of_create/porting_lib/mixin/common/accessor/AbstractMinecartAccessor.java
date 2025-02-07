package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractMinecartEntity.class)
public interface AbstractMinecartAccessor {
	@Invoker("getMaxSpeed")
	double port_lib$getMaxSpeed();
}
