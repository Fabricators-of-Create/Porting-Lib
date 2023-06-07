package io.github.fabricators_of_create.porting_lib.models.mixin;

import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends Entity {
	public IronGolemMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyArgs(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void addBlockPosToParticle(Args args) {
		int x = Mth.floor(this.getX());
		int y = Mth.floor(this.getY() - 0.20000000298023224);
		int z = Mth.floor(this.getZ());
		((BlockParticleOptionExtensions)args.get(0)).setSourcePos(new BlockPos(x, y, z));
	}
}
