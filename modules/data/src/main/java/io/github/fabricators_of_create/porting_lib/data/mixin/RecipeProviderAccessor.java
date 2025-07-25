package io.github.fabricators_of_create.porting_lib.data.mixin;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {
	@Accessor
	PackOutput.PathProvider getRecipePathProvider();

	@Accessor
	PackOutput.PathProvider getAdvancementPathProvider();
}
