package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.common.SlotExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin implements SlotExtension {
	@Shadow
	@Final
	private int slot;

	@Shadow
	@Final
	public Container container;

	private Pair<ResourceLocation, ResourceLocation> port_lib$backgroundPair = null;

	@Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
	private void port_lib$setNoItemIcon(CallbackInfoReturnable<@Nullable Pair<ResourceLocation, ResourceLocation>> cir) {
		if (this.port_lib$backgroundPair != null) {
			cir.setReturnValue(this.port_lib$backgroundPair);
		}
	}

	@Override
	public Slot port_lib$setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		this.port_lib$backgroundPair = Pair.of(atlas, sprite);
		return (Slot) (Object) this;
	}

	@Override
	public int port_lib$getSlotIndex() {
		return slot;
	}

	@Override
	public boolean port_lib$isSameInventory(Slot other) {
		return this.container == other.container;
	}
}
