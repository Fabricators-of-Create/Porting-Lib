package io.github.fabricators_of_create.porting_lib.resources.mixin.porting_lib;

import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;

import io.github.fabricators_of_create.porting_lib.registry.DelegatedHolder;
import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;

import net.minecraft.core.Holder;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

// Yes we really are doing this
@Mixin(DeferredHolder.class)
public abstract class DeferredHolderMixin<R, T extends R> implements Holder<R>, Supplier<T>, DelegatedHolder<R> {

	@Shadow
	@Nullable
	private Holder<R> holder = null;

	@Shadow
	protected abstract void bind(boolean throwOnMissingRegistry);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <Z> @Nullable Z getData(DataMapType<R, Z> type) {
		bind(false);
		return holder == null ? null : holder.getData(type);
	}
}
