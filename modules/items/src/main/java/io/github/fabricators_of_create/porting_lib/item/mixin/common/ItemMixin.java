package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.injects.ItemInjection;
import io.github.fabricators_of_create.porting_lib.item.injects.ItemPropertiesInjection;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements ItemInjection {

	protected boolean port_lib$canRepair;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void setNoRepair(Item.Properties properties, CallbackInfo ci) {
		this.port_lib$canRepair = properties.port_lib$getNoRepair();
	}

	@Override
	public boolean port_lib$canRepair() {
		return this.port_lib$canRepair;
	}

	@Mixin(Item.Properties.class)
	public static class PropertiesMixin implements ItemPropertiesInjection {

		private boolean port_lib$canRepair = true;

		@Override
		public Item.Properties port_lib$setNoRepair() {
			port_lib$canRepair = false;
			return (Item.Properties) (Object) this;
		}

		@Override
		public boolean port_lib$getNoRepair() {
			return port_lib$canRepair;
		}
	}
}
