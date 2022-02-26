package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
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
public abstract class ItemStackMixin implements NBTSerializable {

	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	public void port_lib$onGetMaxCount(CallbackInfoReturnable<Integer> cir) {
		ItemStack self = (ItemStack) (Object) this;
		Item item = self.getItem();
		if (item instanceof CustomMaxCountItem) {
			cir.setReturnValue(((CustomMaxCountItem) item).getItemStackLimit(self));
		}
	}

	@Override
	public CompoundTag port_lib$serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		MixinHelper.<ItemStack>cast(this).save(nbt);
		return nbt;
	}

	@Override
	public void port_lib$deserializeNBT(CompoundTag nbt) {
		MixinHelper.<ItemStack>cast(this).setTag(ItemStack.of(nbt).getTag());
	}
}
