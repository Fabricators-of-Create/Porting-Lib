package xyz.bluspring.forgecapabilities.mixin.common.capabilities;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidBucketWrapper;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;
import xyz.bluspring.forgecapabilities.extensions.capabilities.ItemCapabilityExtension;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item implements ItemCapabilityExtension {
	public BucketItemMixin(Properties properties) {
		super(properties);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack type, @Nullable CompoundTag tag) {
		if (((BucketItem) (Object) this).getClass() == BucketItem.class) {
			return new FluidBucketWrapper(ContainerItemContext.withConstant(type));
		} else {
			return super.initCapabilities(type, tag);
		}
	}
}
