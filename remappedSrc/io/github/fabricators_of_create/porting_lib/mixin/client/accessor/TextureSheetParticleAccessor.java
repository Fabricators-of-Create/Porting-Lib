package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteBillboardParticle.class)
public interface TextureSheetParticleAccessor {
	@Invoker("setSprite")
	void porting_lib$setSprite(Sprite sprite);
}
