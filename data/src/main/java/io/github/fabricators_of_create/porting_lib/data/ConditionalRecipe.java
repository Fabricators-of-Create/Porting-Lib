//package io.github.fabricators_of_create.porting_lib.data;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//
//import com.mojang.serialization.Codec;
//
//import io.github.fabricators_of_create.porting_lib.core.PortingLib;
//import io.github.fabricators_of_create.porting_lib.util.CraftingHelper;
//import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
//import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
//import net.minecraft.core.Registry;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//
//import org.jetbrains.annotations.Nullable;
//
//public class ConditionalRecipe<T extends Recipe<?>> implements RecipeSerializer<T> {
//	public static final Codec<Recipe<?>> CONDITIONAL_RECIPES_CODEC = RecipeManager.CONDITIONAL_DISPATCH.listOf().fieldOf("recipes")
//			.codec().xmap(optionals -> optionals.stream().filter(Optional::isPresent).findFirst().flatMap(Function.identity()).orElse(CraftingHelper.EMPTY_RECIPE),
//					r -> List.of(Optional.of(r)));
//
//	@Override
//	public Codec<T> codec() {
//		return (Codec<T>) CONDITIONAL_RECIPES_CODEC;
//	}
//
//	// Should never get here as it's a wrapper
//	@Override
//	public @Nullable T fromNetwork(FriendlyByteBuf p_44106_) {
//		return null;
//	}
//
//	@Override
//	public void toNetwork(FriendlyByteBuf p_44101_, T p_44102_) {
//
//	}
//}
