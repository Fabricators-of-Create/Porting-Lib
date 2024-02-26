package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.crafting.ShapelessRecipe;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin {
// todo: reevaluate existence
//	@Shadow
//	@Final
//	NonNullList<Ingredient> ingredients;
//
//	private boolean isSimple;
//
//	@Inject(method = "<init>", at = @At("TAIL"))
//	public void port_lib$init(ResourceLocation resourceLocation, String string, ItemStack itemStack, NonNullList<Ingredient> nonNullList, CallbackInfo ci) {
//		this.isSimple = nonNullList.stream().allMatch(IngredientExtensions::isSimple);
//	}
//
//	@Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
//	public void port_lib$matches(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
//		if (!isSimple) {
//			StackedContents stackedcontents = new StackedContents();
//			java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
//			int i = 0;
//
//			for(int j = 0; j < inv.getContainerSize(); ++j) {
//				ItemStack itemstack = inv.getItem(j);
//				if (!itemstack.isEmpty()) {
//					++i;
//					if (isSimple)
//						stackedcontents.accountStack(itemstack, 1);
//					else inputs.add(itemstack);
//				}
//			}
//
//			cir.setReturnValue(i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft((Recipe<?>) this, null) : RecipeMatcher.findMatches(inputs,  this.ingredients) != null));
//		}
//	}
}
