package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

	@Shadow
	@Final
	private World level;

	@Inject(method = "explode", at = @At(value = "NEW", target = "net/minecraft/world/phys/Vec3", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onExplode(CallbackInfo ci, Set<BlockPos> blocks, int i, float j, int k, int l, int d, int q, int e, int r, List<Entity> list) {
		ExplosionEvents.DETONATE.invoker().onDetonate(this.level, (Explosion) (Object) this, list, j);
	}
}
