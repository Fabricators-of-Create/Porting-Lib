package io.github.fabricators_of_create.porting_lib.models.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public abstract Vec3 position();

	@ModifyArg(
			method = "spawnSprintParticle",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
			)
	)
	private ParticleOptions addSourcePos(ParticleOptions options) {
		return BlockParticleOptionExtensions.setSourceFromEntity(options, (Entity) (Object) this);
	}
}
