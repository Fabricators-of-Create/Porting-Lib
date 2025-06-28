package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.extensions.IShearable;
import net.minecraft.world.entity.Shearable;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Shearable.class)
public interface ShearableMixin extends IShearable {
}
