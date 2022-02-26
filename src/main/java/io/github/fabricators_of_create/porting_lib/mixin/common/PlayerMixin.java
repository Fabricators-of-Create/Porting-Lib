package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.LivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.PlayerTickEndCallback;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void port_lib$clientEndOfTickEvent(CallbackInfo ci) {
		PlayerTickEndCallback.EVENT.invoker().onEndOfPlayerTick(MixinHelper.cast(this));
	}

	@ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private void port_lib$onHurt(Args args) {
		DamageSource source = args.get(0);
		float currentAmount = args.get(1);
		float newAmount = LivingEntityEvents.ACTUALLY_HURT.invoker().onHurt(source, this, currentAmount);
		if (newAmount != currentAmount)
			args.set(1, newAmount);
	}
}
