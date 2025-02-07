package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeAccess.class)
public interface BiomeManagerAccessor {
	@Accessor("biomeZoomSeed")
	long port_lib$getBiomeZoomSeed();
}
