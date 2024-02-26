package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureStateShard.class)
public interface TextureStateShardAccessor {
	@Accessor("blur")
	@Mutable
	void port_lib$blur(boolean blur);

	@Accessor("mipmap")
	@Mutable
	void port_lib$mipmap(boolean mipmap);
}
