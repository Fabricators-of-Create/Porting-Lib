package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.serialization.DataResult;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ConditionalOps;
import io.github.fabricators_of_create.porting_lib.resources.conditions.PortingLibConditions;
import io.github.fabricators_of_create.porting_lib.resources.conditions.WithConditions;
import io.github.fabricators_of_create.porting_lib.resources.extensions.ContextAwareReloadListenerExtension;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	@WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;getOrThrow(Ljava/util/function/Function;)Ljava/lang/Object;"))
	private <E, R> R port_lib$tryUseConditionalRecipeDecode(DataResult<R> instance, Function<String, E> stringEFunction, Operation<R> original, @Local RegistryOps<JsonElement> registryOps, @Local Map.Entry<ResourceLocation, JsonElement> entry, @Share("conditionalRegistryOps") LocalRef<ConditionalOps<JsonElement>> conditionalOps) {
		if (conditionalOps.get() == null)
			conditionalOps.set(new ConditionalOps<>(registryOps, ((ContextAwareReloadListenerExtension) this).port_lib$getContext()));

		DataResult<Optional<WithConditions<Recipe<?>>>> decoded = PortingLibConditions.CONDITIONAL_RECIPES_CODEC.parse(conditionalOps.get(), entry.getValue());

		if (decoded.isSuccess()) {
			if (decoded.getOrThrow().isPresent()) {
				if (!decoded.getOrThrow().orElseThrow().conditions().isEmpty())
					return (R) decoded.getOrThrow().orElseThrow().carrier();
			} else {
				throw new JsonParseException("Skipping loading recipe as its conditions were not met");
			}
		}

		return original.call(instance, stringEFunction);
	}
}
