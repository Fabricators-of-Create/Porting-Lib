package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.SlotExtensions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin implements SlotExtensions {
	@Shadow
	@Final
	private int slot;
	@Unique
	private Pair<ResourceLocation, ResourceLocation> port_lib$backgroundPair = null;

	@Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
	private void port_lib$setNoItemIcon(CallbackInfoReturnable<@Nullable Pair<ResourceLocation, ResourceLocation>> cir) {
		if (this.port_lib$backgroundPair != null) {
			cir.setReturnValue(this.port_lib$backgroundPair);
		}
	}

	@Override
	public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		this.port_lib$backgroundPair = Pair.of(atlas, sprite);
		return (Slot) (Object) this;
	}

	@Unique
	@Override
	public int getSlotIndex() {
		return slot;
	}
}
