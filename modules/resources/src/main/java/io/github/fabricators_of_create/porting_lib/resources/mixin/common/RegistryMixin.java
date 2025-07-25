package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.injections.RegistryInjection;
import net.minecraft.core.Registry;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
public interface RegistryMixin<T> extends RegistryInjection<T> {
}
