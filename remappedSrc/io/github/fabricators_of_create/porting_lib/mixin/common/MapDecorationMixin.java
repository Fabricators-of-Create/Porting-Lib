package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.MapDecorationExtensions;
import net.minecraft.item.map.MapIcon;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapIcon.class)
public abstract class MapDecorationMixin implements MapDecorationExtensions {
}
