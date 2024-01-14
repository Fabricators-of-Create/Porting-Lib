package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;

import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightTexture.class)
public class LightTextureMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "updateLightTexture", at = @At(value = "JUMP", opcode = Opcodes.IFLE, ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void adjustLightmap(float partialTicks, CallbackInfo ci, ClientLevel clientlevel, float f, float h, float e, float a, float k, float m, float l, Vector3f vector3f, float f7, Vector3f vector3f1, int i, int j, float f8) {
		clientlevel.effects().adjustLightmapColors(clientlevel, partialTicks, f, f7, f8, j, i, vector3f1);
	}
}
