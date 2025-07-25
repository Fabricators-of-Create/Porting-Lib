package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ConditionalOps;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.conditions.PortingLibConditions;
import io.github.fabricators_of_create.porting_lib.resources.extensions.ContextAwareReloadListenerExtension;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ServerAdvancementManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Function;

@Mixin(ServerAdvancementManager.class)
public abstract class ServerAdvancementManagerMixin {
	@Unique private static final ThreadLocal<ConditionalOps<JsonElement>> port_lib$conditionalOps = new ThreadLocal<>();

	@WrapOperation(method = "method_20723", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow(Ljava/util/function/Function;)Ljava/lang/Object;"))
	private <E, R> R port_lib$tryUseConditionalRecipeDecode(DataResult<R> instance, Function<String, E> stringEFunction, Operation<R> original, @Local(argsOnly = true) RegistryOps<JsonElement> registryOps, @Local(argsOnly = true) JsonElement json) {
		if (port_lib$conditionalOps.get() == null)
			port_lib$conditionalOps.set(new ConditionalOps<>(registryOps, ((ContextAwareReloadListenerExtension) this).port_lib$getContext()));

		Optional<Advancement> decoded = ICondition.getWithWithConditionsCodec(PortingLibConditions.CONDITIONAL_ADVANCEMENTS_CODEC, port_lib$conditionalOps.get(), json);

		if (decoded.isPresent()) {
			return (R) decoded.orElseThrow();
		}

		return original.call(instance, stringEFunction);
	}
}
