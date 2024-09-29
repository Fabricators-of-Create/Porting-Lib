package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

	@Shadow
	@Final
	private Level level;

	@Inject(method = "explode", at = @At(value = "NEW", target = "net/minecraft/world/phys/Vec3", ordinal = 1))
	public void onExplode(CallbackInfo ci, @Local float j, @Local List<Entity> list) {
		ExplosionEvents.DETONATE.invoker().onDetonate(this.level, (Explosion) (Object) this, list, j);
	}
}
