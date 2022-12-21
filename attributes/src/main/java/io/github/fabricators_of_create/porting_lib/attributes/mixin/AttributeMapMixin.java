package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import net.minecraft.core.registries.BuiltInRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

@Mixin(AttributeMap.class)
public class AttributeMapMixin {
	@ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
	private String port_lib$remapOldAttribute(String location) {
		if (BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse(location)).isPresent())
			return location;
		return location.replace("forge", "porting_lib");
	}
}
