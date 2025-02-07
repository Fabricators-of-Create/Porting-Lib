package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;

@Mixin(BlockDustParticle.Factory.class)
public class TerrainParticle$ProviderMixin {
	@ModifyReturnValue(method = "createParticle", at = @At("RETURN"))
	private Particle updateSprite(Particle particle,
								  BlockStateParticleEffect type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		BlockPos source = type.getSourcePos();
		if (source != null && particle instanceof BlockDustParticle terrainParticle) {
			terrainParticle.updateSprite(type.getBlockState(), source);
		}
		return particle;
	}
}
