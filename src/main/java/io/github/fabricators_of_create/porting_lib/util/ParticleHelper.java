package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.ParticleAccessor;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.TextureSheetParticleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public final class ParticleHelper {
	public static void setStoppedByCollision(Particle particle, boolean bool) {
		((ParticleAccessor) particle).port_lib$stoppedByCollision(bool);
	}

	public static void setSprite(TextureSheetParticle particle, TextureAtlasSprite sprite) {
		((TextureSheetParticleAccessor) particle).porting_lib$setSprite(sprite);
	}

	public static void updateSprite(TextureSheetParticle particle, BlockState state) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer()
				.getBlockModelShaper().getParticleIcon(state);
		setSprite(particle, sprite);
	}

	private ParticleHelper() {}
}
