package io.github.fabricators_of_create.porting_lib.brewing.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipe;
import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;
import io.github.fabricators_of_create.porting_lib.brewing.IBrewingRecipe;
import io.github.fabricators_of_create.porting_lib.brewing.RegisterBrewingRecipesEvent;
import io.github.fabricators_of_create.porting_lib.brewing.ext.PotionBrewingBuilderExt;
import io.github.fabricators_of_create.porting_lib.brewing.ext.PotionBrewingExt;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;

import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin implements PotionBrewingExt {
	@Shadow
	protected abstract boolean isContainer(ItemStack itemStack);

	private BrewingRecipeRegistry porting_lib$registry;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void createEmptyRegistry(final CallbackInfo ci) {
		this.porting_lib$registry = new BrewingRecipeRegistry(List.of()); // Create an empty builder in case a mod doesn't use the builder
	}

	@Override
	public boolean isInput(ItemStack stack) {
		return this.porting_lib$registry.isValidInput(stack) || isContainer(stack);
	}

	@Override
	public List<IBrewingRecipe> getRecipes() {
		return porting_lib$registry.recipes();
	}

	@Override
	public void setBrewingRegistry(BrewingRecipeRegistry registry) {
		this.porting_lib$registry = registry;
	}

	@Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
	private void checkMixRegistry(ItemStack container, ItemStack mix, CallbackInfoReturnable<Boolean> cir) {
		if (porting_lib$registry.hasOutput(container, mix)) cir.setReturnValue(true);
	}

	@Inject(method = "mix", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
	), cancellable = true)
	private void doMix(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<ItemStack> cir) {
		var customMix = porting_lib$registry.getOutput(itemStack2, itemStack); // Parameters are swapped compared to what vanilla passes!
		if (!customMix.isEmpty()) cir.setReturnValue(customMix);
	}

	@Inject(method = "bootstrap", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/alchemy/PotionBrewing;addVanillaMixes(Lnet/minecraft/world/item/alchemy/PotionBrewing$Builder;)V",
			shift = At.Shift.AFTER
	), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void fireRegisterEvent(FeatureFlagSet featureFlagSet, CallbackInfoReturnable<PotionBrewing> cir, PotionBrewing.Builder builder) {
		new RegisterBrewingRecipesEvent(builder).sendEvent();
	}

	@Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
	private void checkRegistryForValidIngredient(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (this.porting_lib$registry.isValidIngredient(itemStack))
			cir.setReturnValue(true);
	}

	@Mixin(value = PotionBrewing.Builder.class, priority = 300)
	public static class BuilderMixin implements PotionBrewingBuilderExt {
		private final List<IBrewingRecipe> porting_lib$recipes = new ArrayList<>();

		@Override
		public void addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
			addRecipe(new BrewingRecipe(input, ingredient, output));
		}

		@Override
		public void addRecipe(IBrewingRecipe recipe) {
			this.porting_lib$recipes.add(recipe);
		}

		@ModifyReturnValue(method = "build", at = @At("RETURN"))
		private PotionBrewing addCustomRecipes(PotionBrewing original) {
			original.setBrewingRegistry(new BrewingRecipeRegistry(porting_lib$recipes));
			return original;
		}
	}
}
