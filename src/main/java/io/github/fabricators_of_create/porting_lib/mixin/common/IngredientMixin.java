package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.crafting.VanillaIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientExtensions {

    @Shadow
	private
	ItemStack[] itemStacks;

    @Shadow
	private IntList stackingIds;

    @Shadow
	@Final
	public static Ingredient EMPTY;

    @Inject(method = "toNetwork", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V"), cancellable = true)
    private void toNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (!this.isVanilla()) {
            CraftingHelper.write(buffer, (Ingredient) (Object) this);
            ci.cancel();
        }
    }

    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void fromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
        int size = buffer.readVarInt();
        if (size == -1)
			cir.setReturnValue(CraftingHelper.getIngredient(buffer.readResourceLocation(), buffer));
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!isVanilla()) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
            return VanillaIngredientSerializer.INSTANCE;
    }

    public final boolean isVanilla() {
        return this.getClass().equals(Ingredient.class);
    }

    @Override
    public boolean isSimple() {
        return (Object) this == EMPTY;
    }

    @Override
    public void invalidate() {
        itemStacks = null;
        stackingIds = null;
    }
}
