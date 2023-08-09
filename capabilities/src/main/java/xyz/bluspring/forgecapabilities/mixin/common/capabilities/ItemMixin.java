package xyz.bluspring.forgecapabilities.mixin.common.capabilities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.transfer.item.ShulkerItemStackInvWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;
import xyz.bluspring.forgecapabilities.extensions.capabilities.ItemCapabilityExtension;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemCapabilityExtension {
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return ShulkerItemStackInvWrapper.createDefaultProvider(stack);
	}
}
