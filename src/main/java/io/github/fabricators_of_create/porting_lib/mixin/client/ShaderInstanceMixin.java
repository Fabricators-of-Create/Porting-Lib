package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
@Mixin(value = ShaderInstance.class, priority = 1500) // apply after FAPI so we get the modified ID below. FAPI doesn't have handling for an already fixed one.
public abstract class ShaderInstanceMixin {
	@Shadow
	@Final
	private String name;

	// we still need to do this despite FAPI's API since it only checks for FabricShader
	@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V", ordinal = 0))
	private String fixId(String path) {
		if (!path.contains(":")) {
			return path;
		}
		ResourceLocation split = new ResourceLocation(name);
		return split.getNamespace() + ":shaders/core/" + split.getPath() + ".json";
	}

	// note: do not need to modify getOrCreate as FAPI does it
}
