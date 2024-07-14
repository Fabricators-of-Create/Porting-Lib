package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingChangeTargetEvent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Shadow
	@Nullable
	private LivingEntity target;

	@ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
	private LivingEntity port_lib$onChangeTarget(LivingEntity value) {
		LivingChangeTargetEvent changeTargetEvent = EntityHooks.onLivingChangeTarget(MixinHelper.cast(this), value, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
		if(!changeTargetEvent.isCanceled()) {
			return changeTargetEvent.getNewTarget();
		}
		return this.target;
	}
}
