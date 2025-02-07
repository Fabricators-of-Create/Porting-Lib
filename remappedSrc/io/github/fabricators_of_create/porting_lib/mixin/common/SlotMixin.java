package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.SlotExtensions;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
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
	private Pair<Identifier, Identifier> port_lib$backgroundPair = null;

	@Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
	private void port_lib$setNoItemIcon(CallbackInfoReturnable<@Nullable Pair<Identifier, Identifier>> cir) {
		if (this.port_lib$backgroundPair != null) {
			cir.setReturnValue(this.port_lib$backgroundPair);
		}
	}

	@Override
	public Slot setBackground(Identifier atlas, Identifier sprite) {
		this.port_lib$backgroundPair = Pair.of(atlas, sprite);
		return (Slot) (Object) this;
	}

	@Unique
	@Override
	public int getSlotIndex() {
		return slot;
	}
}
