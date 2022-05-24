package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.crafting.RecipeMatcher;
import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {

	@Shadow
	@Final
	private NonNullList<Ingredient> ingredients;
	private boolean isSimple;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$init(ResourceLocation resourceLocation, String string, ItemStack itemStack, NonNullList<Ingredient> nonNullList, CallbackInfo ci) {
		this.isSimple = nonNullList.stream().allMatch(IngredientExtensions::isSimple);
	}

	@Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
	public void port_lib$matches(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
		if (!isSimple) {
			StackedContents stackedcontents = new StackedContents();
			java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
			int i = 0;

			for(int j = 0; j < inv.getContainerSize(); ++j) {
				ItemStack itemstack = inv.getItem(j);
				if (!itemstack.isEmpty()) {
					++i;
					if (isSimple)
						stackedcontents.accountStack(itemstack, 1);
					else inputs.add(itemstack);
				}
			}

			cir.setReturnValue(i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft((Recipe<?>) this, null) : RecipeMatcher.findMatches(inputs,  this.ingredients) != null));
		}
	}
}
