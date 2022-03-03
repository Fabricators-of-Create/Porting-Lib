package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;

import io.github.fabricators_of_create.porting_lib.item.ToolActionCheckingItem;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.CustomMaxCountItem;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.NBTSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements NBTSerializable, ItemStackExtensions {

	@Shadow
	public abstract CompoundTag save(CompoundTag compoundTag);

	@Shadow
	public abstract void setTag(@Nullable CompoundTag compoundTag);

	@Shadow
	public abstract Item getItem();

	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	public void port_lib$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		this.save(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.setTag(ItemStack.of(nbt).getTag());
	}

	@Override
	public boolean canPerformAction(ToolAction toolAction) {
		if (this.getItem() instanceof ToolActionCheckingItem checking) {
			return checking.canPerformAction((ItemStack) (Object) this, toolAction);
		}
		return false;
	}
}
