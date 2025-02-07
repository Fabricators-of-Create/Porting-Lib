package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
	@Shadow
	@Final
	private Map<Identifier, AbstractTexture> byPath;

	@Inject(
			method = "release",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/TextureUtil;releaseTextureId(I)V",
					shift = At.Shift.BEFORE,
					remap = false
			)
	)
	public void port_lib$fixRelease(Identifier path, CallbackInfo ci) {
		this.byPath.remove(path);
	}
}
