package io.github.fabricators_of_create.porting_lib.mixin.capabilities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.ItemCapabilityExtension;
import io.github.fabricators_of_create.porting_lib.transfer.item.ShulkerItemStackInvWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemCapabilityExtension {
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return ShulkerItemStackInvWrapper.createDefaultProvider(stack);
	}
}
