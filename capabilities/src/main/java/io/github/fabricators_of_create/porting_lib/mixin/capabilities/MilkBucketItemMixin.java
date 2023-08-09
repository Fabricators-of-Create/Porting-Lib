package io.github.fabricators_of_create.porting_lib.mixin.capabilities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.ItemCapabilityExtension;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidBucketWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin implements ItemCapabilityExtension {
	@Override
	public ICapabilityProvider initCapabilities(ItemStack type, @Nullable CompoundTag tag) {
		return new FluidBucketWrapper(ContainerItemContext.withConstant(type));
	}
}
