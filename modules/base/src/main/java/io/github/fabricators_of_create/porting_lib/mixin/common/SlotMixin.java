package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.common.SlotExtension;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
@Implements(@Interface(iface = SlotExtension.class, prefix = "port_lib$"))
public abstract class SlotMixin {
	@Shadow
	@Final
	private int slot;

	@Shadow
	@Final
	public Container container;

	private Pair<Identifier, Identifier> port_lib$backgroundPair = null;

	@Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
	private void setNoItemIcon(CallbackInfoReturnable<@Nullable Pair<Identifier, Identifier>> cir) {
		if (this.port_lib$backgroundPair != null) {
			cir.setReturnValue(this.port_lib$backgroundPair);
		}
	}

	@Intrinsic
	public Slot port_lib$setBackground(Identifier atlas, Identifier sprite) {
		this.port_lib$backgroundPair = Pair.of(atlas, sprite);
		return (Slot) (Object) this;
	}

	@Intrinsic
	public int port_lib$getSlotIndex() {
		return slot;
	}

	@Intrinsic
	public boolean port_lib$isSameInventory(Slot other) {
		return this.container == other.container;
	}
}
