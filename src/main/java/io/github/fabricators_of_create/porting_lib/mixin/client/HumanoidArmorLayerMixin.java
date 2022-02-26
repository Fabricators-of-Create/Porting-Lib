package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

@Environment(EnvType.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {

	@Unique
	private static final ResourceLocation port_lib$copperArmorLocation = new ResourceLocation("create", "textures/models/armor/copper.png");

	@Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
	private void port_lib$getArmorLocation(ArmorItem armorItem, boolean bl, String string, CallbackInfoReturnable<ResourceLocation> cir) {
		if (armorItem.getMaterial().getName().equals("copper")) {
			cir.setReturnValue(port_lib$copperArmorLocation);
		}
	}
}
