package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import net.minecraft.core.Holder;

import net.minecraft.core.HolderLookup;

import net.minecraft.core.HolderOwner;

import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.Reference.class)
public abstract class HolderReferenceMixin<T> implements Holder<T> {
	@Shadow
	@Final
	private HolderOwner<T> owner;

	@Shadow
	public abstract ResourceKey<T> key();

	@Nullable
	public <A> A getData(DataMapType<T, A> type) {
		if (owner instanceof HolderLookup.RegistryLookup<T> lookup) {
			return lookup.getData(type, key());
		}
		return null;
	}
}
