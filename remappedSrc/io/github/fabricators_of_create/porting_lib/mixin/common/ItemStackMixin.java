package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;

import io.github.fabricators_of_create.porting_lib.util.DamageableItem;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.CustomMaxCountItem;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements INBTSerializable<NbtCompound>, ItemStackExtensions {

	@Shadow
	public abstract NbtCompound save(NbtCompound compoundTag);

	@Shadow
	public abstract void setTag(@Nullable NbtCompound compoundTag);

	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract boolean hasTag();

	@Shadow
	private @Nullable NbtCompound tag;

	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	public void port_lib$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

	@Override
	public NbtCompound serializeNBT() {
		NbtCompound nbt = new NbtCompound();
		this.save(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NbtCompound nbt) {
		this.setTag(ItemStack.fromNbt(nbt).getNbt());
	}

	@Unique
	@Override
	public boolean canPerformAction(ToolAction toolAction) {
		return getItem().canPerformAction((ItemStack) (Object) this, toolAction);
	}

	@Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemSetDamage(int damage, CallbackInfo ci) {
		if(getItem() instanceof DamageableItem damagableItem) {
			damagableItem.setDamage((ItemStack) (Object) this, damage);
			ci.cancel();
		}
	}

	@Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getMaxDamage((ItemStack) (Object) this));
		}
	}

	@Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getDamage((ItemStack) (Object) this));
		}
	}

	@ModifyReturnValue(method = "getHideFlags", at = @At(value = "RETURN", ordinal = 1))
	public int port_lib$itemFlags(int val) {
		if (val == 0 && !(this.hasTag() && this.tag.contains("HideFlags", 99)))
			return getItem().getDefaultTooltipHideFlags(MixinHelper.cast(this));
		return val;
	}
}
