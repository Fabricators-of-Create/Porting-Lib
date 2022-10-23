package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.world.entity.monster.Slime;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Slime.class)
public interface SlimeAccessor {
	@Invoker("setSize")
	void port_lib$setSize(int i, boolean bl);
}
