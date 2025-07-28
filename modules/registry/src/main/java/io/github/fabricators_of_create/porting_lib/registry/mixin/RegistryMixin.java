package io.github.fabricators_of_create.porting_lib.registry.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.registry.DelegatedHolder;

import io.github.fabricators_of_create.porting_lib.registry.injections.RegistryInjection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

@Mixin(Registry.class)
public interface RegistryMixin<T> extends RegistryInjection<T> {
	@Definition(id = "value", local = @Local(type = Holder.class, argsOnly = true))
	@Definition(id = "Reference", type = Holder.Reference.class)
	@Expression("value instanceof Reference")
	@WrapOperation(method = "safeCastToReference", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean checkDelegateHolder(Object object, Operation<Boolean> original, @Local(ordinal = 0, argsOnly = true) LocalRef<Holder<?>> valueRef) {
		if (object instanceof DelegatedHolder<?> ref) {
			Holder<?> delegated = ref.getDelegate();
			valueRef.set(delegated);
			return original.call(delegated);
		}
		return original.call(object);
	}
}
