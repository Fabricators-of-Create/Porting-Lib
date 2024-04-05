package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
	@Shadow
	public abstract void setPierceLevel(byte b);

	@Unique
	private final IntOpenHashSet ignoredEntities = new IntOpenHashSet();

	public AbstractArrowMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
	private void dontHitIgnored(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (ignoredEntities.contains(entity.getId()))
			cir.setReturnValue(false);
	}

	@WrapWithCondition(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;onHit(Lnet/minecraft/world/phys/HitResult;)V"
			)
	)
	private boolean onImpact(AbstractArrow arrow, HitResult result,
							 @Local LocalRef<EntityHitResult> entityHit,
							 @Share("isCanceled") LocalBooleanRef isCanceled) {
		// note: Forge additionally checks that the result != MISS before running any logic.
		// this is likely left over from an earlier version.
		// this behavior is intentionally not replicated because 1. compat and 2. it probably doesn't matter.
		ProjectileImpactEvent event = new ProjectileImpactEvent(arrow, result);
		event.sendEvent();

		boolean canceled = switch (event.getImpactResult()) {
			case SKIP_ENTITY -> {
				if (result.getType() != HitResult.Type.ENTITY) {
					// didn't hit an entity, do vanilla logic
					yield false;
				} else {
					this.ignoredEntities.add(entityHit.get().getEntity().getId());
					// cancel and skip further processing
					entityHit.set(null);
					yield true;
				}
			}
			case STOP_AT_CURRENT_NO_DAMAGE -> {
				this.discard();
				// cancel and skip further processing
				entityHit.set(null);
				yield true;
			}
			case STOP_AT_CURRENT -> {
				this.setPierceLevel((byte) 0);
				// does not cancel
				yield false;
			}
			case DEFAULT -> false; // don't cancel, do vanilla logic
		};

		if (canceled) {
			// need to save this. have to separately cancel the hasImpulse set
			isCanceled.set(true);
			return false;
		}

		return true;
    }

	@WrapWithCondition(
			method = "tick",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hasImpulse:Z"
			)
	)
	private boolean handleImpulse(AbstractArrow instance, boolean value,
								  @Share("isCanceled") LocalBooleanRef isCanceled) {
		boolean canceled = isCanceled.get();
		isCanceled.set(false); // reset, don't leak state
		return !canceled;
	}
}
