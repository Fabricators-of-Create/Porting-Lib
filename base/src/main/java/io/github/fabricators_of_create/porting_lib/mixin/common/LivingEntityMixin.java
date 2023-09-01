package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.common.PotionEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@SuppressWarnings("ConstantConditions")
@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(
			method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
					shift = Shift.BY,
					by = 3,
					remap = false
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$addEffect(MobEffectInstance newEffect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir,
								   MobEffectInstance oldEffect) {
		PotionEvents.POTION_ADDED.invoker().onPotionAdded((LivingEntity) (Object) this, newEffect, oldEffect, source);
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	public void port_lib$canBeAffected(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		InteractionResult result = PotionEvents.POTION_APPLICABLE.invoker().onPotionApplicable((LivingEntity) (Object) this, effect);
		if (result != InteractionResult.PASS)
			cir.setReturnValue(result == InteractionResult.SUCCESS);
	}
}
