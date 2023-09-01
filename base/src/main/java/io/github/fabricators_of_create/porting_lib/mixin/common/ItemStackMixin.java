package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializableCompound;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.ItemStackExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements INBTSerializableCompound, ItemStackExtensions {

	@Shadow
	public abstract CompoundTag save(CompoundTag compoundTag);

	@Shadow
	public abstract void setTag(@Nullable CompoundTag compoundTag);

	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract boolean hasTag();

	@Shadow
	private @Nullable CompoundTag tag;

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



	@ModifyReturnValue(method = "getHideFlags", at = @At(value = "RETURN", ordinal = 1))
	public int port_lib$itemFlags(int val) {
		if (val == 0 && !(this.hasTag() && this.tag.contains("HideFlags", 99)))
			return getItem().getDefaultTooltipHideFlags(MixinHelper.cast(this));
		return val;
	}
}
