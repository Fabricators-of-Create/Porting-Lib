package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.ParticleAccessor;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TextureSheetParticleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;

@Environment(EnvType.CLIENT)
public final class ParticleHelper {
	public static void setStoppedByCollision(Particle particle, boolean bool) {
		((ParticleAccessor) particle).port_lib$stoppedByCollision(bool);
	}

	public static void setSprite(SpriteBillboardParticle particle, Sprite sprite) {
		((TextureSheetParticleAccessor) particle).porting_lib$setSprite(sprite);
	}

	public static void updateSprite(SpriteBillboardParticle particle, BlockState state) {
		Sprite sprite = MinecraftClient.getInstance().getBlockRenderManager()
				.getModels().getModelParticleSprite(state);
		setSprite(particle, sprite);
	}

	private ParticleHelper() {}
}
