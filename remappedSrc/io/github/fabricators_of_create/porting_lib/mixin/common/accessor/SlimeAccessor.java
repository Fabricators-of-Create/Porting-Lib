package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SlimeEntity.class)
public interface SlimeAccessor {
	@Invoker("setSize")
	void port_lib$setSize(int i, boolean bl);
}
