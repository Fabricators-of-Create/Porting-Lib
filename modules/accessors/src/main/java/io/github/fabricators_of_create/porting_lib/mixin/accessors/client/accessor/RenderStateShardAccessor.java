package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import net.minecraft.client.renderer.RenderStateShard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
	@Accessor("setupState")
	@Mutable
	void port_lib$setupState(Runnable setupState);

	@Accessor("clearState")
	@Mutable
	void port_lib$clearState(Runnable clearState);
}
