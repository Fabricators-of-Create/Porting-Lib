package io.github.fabricators_of_create.porting_lib.models.mixin;

import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextureSheetParticle.class)
public interface TextureSheetParticleAccessor {
	@Invoker("setSprite")
	void callSetSprite(TextureAtlasSprite sprite);
}
