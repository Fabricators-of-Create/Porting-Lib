package io.github.fabricators_of_create.porting_lib.registry.mixin;

import io.github.fabricators_of_create.porting_lib.registry.injections.HolderInjection;
import net.minecraft.core.Holder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.class)
public interface HolderMixin<T> extends HolderInjection<T> {
	@Mixin(Holder.Reference.class)
	public abstract static class ReferenceMixin<T> implements Holder<T> {
		@Shadow
		@Nullable
		private ResourceKey<T> key;

		@Shadow
		@Final
		private HolderOwner<T> owner;

		@Nullable
		@Override
		public HolderLookup.RegistryLookup<T> port_lib$unwrapLookup() {
			return this.owner instanceof HolderLookup.RegistryLookup<T> rl ? rl : null;
		}

		@Nullable
		@Override
		public ResourceKey<T> port_lib$getKey() {
			return this.key;
		}
	}
}
