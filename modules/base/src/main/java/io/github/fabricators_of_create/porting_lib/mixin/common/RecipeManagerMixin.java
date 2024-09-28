package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Map;
import java.util.function.Function;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.world.item.crafting.RecipeType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
	public void port_lib$captureRecipe(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, @Local Recipe<?> recipe, @Share("capturedRecipe") LocalRef<Recipe<?>> capturedRecipe) {
		capturedRecipe.set(recipe);
	}
	@Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 1, shift = At.Shift.AFTER))
	public void port_lib$invalidateRecipe(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, @Local Recipe<?> recipe, @Share("capturedRecipe") LocalRef<Recipe<?>> capturedRecipe) {
		capturedRecipe.set(null);
	}

	@WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;getType()Lnet/minecraft/world/item/crafting/RecipeType;"))
	public RecipeType<?> port_lib$nullCompute(Recipe<?> recipe, Operation<RecipeType<?>> recipeTypeOperation) {
		if (recipe == null)
			return null;
		return recipeTypeOperation.call(recipe);
	}

	@WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
	public Object port_lib$allowNullMap(Map<RecipeType<?>, ImmutableMap.Builder<ResourceLocation, Recipe<?>>> map, Object obj, Function function, Operation<Map> operation, @Share("capturedRecipe") LocalRef<Recipe<?>> capturedRecipe) {
		if (capturedRecipe.get() == null)
			return ImmutableMap.builder();
		return operation.call(map, obj, function);
	}

	@WrapWithCondition(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
	public boolean port_lib$allowNullRecipe(ImmutableMap.Builder<ResourceLocation, Recipe<?>> self, Object key, Object val, @Share("capturedRecipe") LocalRef<Recipe<?>> capturedRecipe) {
		return capturedRecipe.get() != null;
	}
}
