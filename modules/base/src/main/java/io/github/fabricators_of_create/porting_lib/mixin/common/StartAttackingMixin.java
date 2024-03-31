package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents.ChangeTarget.ChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents.ChangeTarget.ChangeTargetEvent.LivingTargetType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;

@Mixin(StartAttacking.class)
public class StartAttackingMixin {
	@Inject(
			method = "method_47123(Ljava/util/function/Predicate;Ljava/util/function/Function;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"
			),
			cancellable = true
	)
	private static <E extends Mob> void port_lib$onChangeTarget(Predicate<E> predicate, Function<E, Optional<? extends LivingEntity>> function, MemoryAccessor<Const.Mu<Unit>, LivingEntity> memoryAccessor, MemoryAccessor<OptionalBox.Mu, Long> memoryAccessor2, ServerLevel serverLevel, Mob mob, long l, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity livingEntity, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		changeTargetEvent.set(new ChangeTargetEvent(mob, livingEntity, LivingTargetType.BEHAVIOR_TARGET));
		changeTargetEvent.get().sendEvent();
		if (changeTargetEvent.get().isCanceled())
			cir.setReturnValue(false);
	}

	@WrapOperation(
			method = "method_47123(Ljava/util/function/Predicate;Ljava/util/function/Function;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;set(Ljava/lang/Object;)V"
			)
	)
	private static void port_lib$wrapToChangeTarget(MemoryAccessor<Const.Mu<Unit>, LivingEntity> instance, Object object, Operation<Void> original, @Share("changeTargetEvent") LocalRef<ChangeTargetEvent> changeTargetEvent) {
		original.call(instance, changeTargetEvent.get().getNewTarget());
	}
}
