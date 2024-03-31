package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

@Mixin(StartAttacking.class)
public class StartAttackingMixin {
	@Inject(method = "setAttackTarget",at = @At("HEAD"), cancellable = true)
	private static <E extends Mob> void port_lib$onChangeTarget(E mob, LivingEntity attackTarget, CallbackInfo ci, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		changeTargetEvent.set(new LivingEntityEvents.ChangeTarget.ChangeTargetEvent(mob, attackTarget, LivingTargetType.BEHAVIOR_TARGET));
		changeTargetEvent.get().sendEvent();

		if (changeTargetEvent.get().isCanceled())
			ci.cancel();
	}

	@WrapOperation(method = "setAttackTarget",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"))
	private static <U> void port_lib$wrapToChangeTarget(Brain<?> instance, MemoryModuleType<U> memoryType, U memory, Operation<Void> original, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		// Ignore the warning; It's fine; The problem is just that mcdev can't handle generics
		original.call(instance, memoryType, changeTargetEvent.get().getNewTarget());
	}

	@Inject(method = "setAttackTarget", at = @At("TAIL"))
	private static <E extends Mob> void port_lib$onAfterChangeTarget(E attackTarget, LivingEntity owner, CallbackInfo ci, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget(owner, changeTargetEvent.get().getNewTarget());
	}
}
