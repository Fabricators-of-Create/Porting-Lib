package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import io.github.fabricators_of_create.porting_lib.resources.injections.RegistryLookupInjection;
import net.minecraft.core.HolderLookup;

import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HolderLookup.RegistryLookup.class)
public interface HolderLookupRegistryLookupMixin<T> extends RegistryLookupInjection<T> {

	@Mixin(HolderLookup.RegistryLookup.Delegate.class)
	public interface DelegateMixin<T> extends RegistryLookupInjection<T> {
		@Shadow
		HolderLookup.RegistryLookup<T> parent();

		@Override
		@Nullable
		default <A> A getData(DataMapType<T, A> attachment, ResourceKey<T> key) {
			return parent().getData(attachment, key);
		}
	}
}
