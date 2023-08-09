package io.github.fabricators_of_create.porting_lib.mixin.capabilities;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.capabilities.Capability;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityDispatcher;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityProvider;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityProviderWorkaround;
import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.CapabilityProviderExtension;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.InitializableCapabilityExtension;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.ItemStackCapabilityProviderImpl;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements CapabilityProviderExtension, ItemStackCapabilityProviderImpl, ItemStackExtensions, InitializableCapabilityExtension<ItemStack> {
	@Shadow
	public abstract Item getItem();

	private CompoundTag capNBT;

	private final CapabilityProviderWorkaround<ItemStack> workaround = new CapabilityProviderWorkaround<>(ItemStack.class, (ItemStack) (Object) this);

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return workaround.getCapability(cap, side);
	}

	@Override
	public CapabilityProviderWorkaround<ItemStack> port_lib$getWorkaround() {
		return workaround;
	}

	@Override
	public boolean areCapsCompatible(CapabilityProvider<ItemStack> other) {
		return workaround.areCapsCompatible(other);
	}

	@Override
	public boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
		return workaround.areCapsCompatible(other);
	}

	@Override
	public void invalidateCaps() {
		workaround.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		workaround.reviveCaps();
	}

	@Override
	public void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
		workaround.invokeGatherCapabilities(parent);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;I)V", at = @At("TAIL"))
	private void port_lib$initCapabilities(ItemLike item, int count, CallbackInfo ci) {
		this.initCapabilities();
	}

	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
	private void port_lib$loadCapabilities(CompoundTag tag, CallbackInfo ci) {
		this.capNBT = tag.contains("ForgeCaps") ? tag.getCompound("ForgeCaps") : null;
		this.initCapabilities();
	}

	@Override
	public void initCapabilities() {
		this.gatherCapabilities(() -> this.getItem().initCapabilities((ItemStack) (Object) this, this.capNBT));
		if (this.capNBT != null)
			this.deserializeCaps(this.capNBT);
	}
}
