package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.ChangeTarget.ChangeTargetEvent.LivingTargetType;
import io.github.fabricators_of_create.porting_lib.event.common.MobEntitySetTargetCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.StartAttacking;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StartAttacking.class)
public class StartAttackingMixin<E extends Mob> {
	@Inject(method = "setAttackTarget",at = @At("HEAD"), cancellable = true)
	private void port_lib$onChangeTarget(E attackTarget, LivingEntity owner, CallbackInfo ci, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		changeTargetEvent.set(new LivingEntityEvents.ChangeTarget.ChangeTargetEvent(attackTarget, owner, LivingTargetType.BEHAVIOR_TARGET));
		changeTargetEvent.get().sendEvent();
		if (changeTargetEvent.get().isCanceled())
			ci.cancel();
	}

	@WrapOperation(method = "setAttackTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"))
	private <U> void port_lib$wrapToChangeTarget(Brain<?> instance, MemoryModuleType<U> memoryType, U memory, Operation<Void> original, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		// Ignore the warning, it's fine just that mcdev can't handle generics
		original.call(instance, memoryType, changeTargetEvent.get().getNewTarget());
	}

	@Inject(method = "setAttackTarget", at = @At("TAIL"))
	private void port_lib$onAfterChangeTarget(E attackTarget, LivingEntity owner, CallbackInfo ci, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget(owner, changeTargetEvent.get().getNewTarget());
	}
}
