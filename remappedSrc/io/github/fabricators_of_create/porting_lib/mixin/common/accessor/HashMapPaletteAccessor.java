package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiMapPalette.class)
public interface HashMapPaletteAccessor<T> {
	@Accessor("values")
	Int2ObjectBiMap<T> port_lib$getValues();
}
