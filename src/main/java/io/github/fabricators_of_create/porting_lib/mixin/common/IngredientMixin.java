package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.google.gson.JsonElement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.crafting.IIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.crafting.IngredientInvalidator;
import io.github.fabricators_of_create.porting_lib.crafting.VanillaIngredientSerializer;
import io.github.fabricators_of_create.porting_lib.extensions.IngredientExtensions;

import net.minecraft.world.item.crafting.Ingredient.Value;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(value = Ingredient.class, priority = 2340) // magic random number to make sure we apply in the correct order
public abstract class IngredientMixin implements IngredientExtensions {

    @Shadow
	private
	ItemStack[] itemStacks;

    @Shadow
	private IntList stackingIds;

    @Shadow
	@Final
	public static Ingredient EMPTY;

	@Shadow
	@Final
	private Value[] values;

	@Unique
	private boolean port_lib$simple;
	@Unique
	private final boolean port_lib$vanilla = this.getClass().equals(Ingredient.class);

	@Unique
	private int invalidationCounter;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void port_lib$init(Stream<? extends Ingredient.Value> stream, CallbackInfo ci) {
		port_lib$simple = Arrays.stream(values).noneMatch(list -> list.getItems().stream().anyMatch(stack -> stack.getItem().canBeDepleted()));
	}

    @Inject(method = "toNetwork", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/item/crafting/Ingredient;dissolve()V"), cancellable = true)
    private void port_lib$toNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
		buffer.writeBoolean(isVanilla());
		if (!this.isVanilla()) {
			CraftingHelper.write(buffer, (Ingredient) (Object) this);
			ci.cancel();
		}
    }

	/**
	 * @author AlphaMode
	 * @reason help me...
	 */
	@Overwrite
	public static Ingredient fromNetwork(FriendlyByteBuf p_43941_) {
		var size = p_43941_.readVarInt();
		if (size == -1) return CraftingHelper.getIngredient(p_43941_.readResourceLocation(), p_43941_);
		return Ingredient.fromValues(Stream.generate(() -> new Ingredient.ItemValue(p_43941_.readItem())).limit(size));
	}


	@Inject(method = "fromJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;isJsonObject()Z"), cancellable = true)
	private static void port_lib$fromJson(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		Ingredient ret = CraftingHelper.getIngredient(json);
		if (ret != null) cir.setReturnValue(ret);
	}

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!port_lib$vanilla) throw new IllegalStateException("Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
            return VanillaIngredientSerializer.INSTANCE;
    }

    @Override
    public boolean isSimple() {
        return (Object) this == EMPTY || port_lib$simple;
    }

	@Override
	public final boolean isVanilla() {
		return port_lib$vanilla;
	}

	@Override
    public void invalidate() {
        itemStacks = null;
        stackingIds = null;
    }

	@Override
	public final void markValid() {
		this.invalidationCounter = IngredientInvalidator.INVALIDATION_COUNTER.get();
	}

	@Override
	public final boolean checkInvalidation() {
		int currentInvalidationCounter = IngredientInvalidator.INVALIDATION_COUNTER.get();
		if (this.invalidationCounter != currentInvalidationCounter) {
			invalidate();
			return true;
		}
		return false;
	}
}
