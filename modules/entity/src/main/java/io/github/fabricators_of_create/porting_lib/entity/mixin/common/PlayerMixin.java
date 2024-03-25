package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.CriticalHitEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerTickEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.AttackEntityEvent;
import io.github.fabricators_of_create.porting_lib.entity.extensions.PlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 500)
public abstract class PlayerMixin extends LivingEntity implements PlayerExtension {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void port_lib$playerStartTickEvent(CallbackInfo ci) {
		PlayerTickEvents.START.invoker().onStartOfPlayerTick((Player) (Object) this);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void port_lib$playerEndTickEvent(CallbackInfo ci) {
		PlayerTickEvents.END.invoker().onEndOfPlayerTick((Player) (Object) this);
	}

	@Inject(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
	public void port_lib$onEntityInteract(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult cancelResult = EntityInteractCallback.EVENT.invoker().onEntityInteract((Player) (Object) this, hand, entityToInteractOn);
		if (cancelResult != null) cir.setReturnValue(cancelResult);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	public void port_lib$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingAttackEvent event = new LivingAttackEvent(this, source, amount);
		event.sendEvent();
		if (event.isCanceled())
			cir.setReturnValue(false);
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float port_lib$onHurt(float amount, DamageSource source, float amount2) {
		return LivingEntityEvents.HURT.invoker().onHurt(source, this, amount);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"))
	private int port_lib$xpChange(int experience) {
		PlayerEvents.XpChange xpChange = new PlayerEvents.XpChange((Player) (Object) this, experience);
		xpChange.sendEvent();
		return xpChange.getAmount();
	}

	@ModifyVariable(
			method = "attack",
			slice = @Slice(
				from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1),
				to = @At(value = "CONSTANT", args = "floatValue=1.5F")
			),
			at = @At(value = "STORE", opcode = Opcodes.ISTORE), index = 8
	)
	private boolean modifyResult(boolean value, @Share("original") LocalBooleanRef vanilla) {
		vanilla.set(value);
		return true;
	}

	@ModifyVariable(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5F"), index = 8)
	private boolean modifyVanillaResult(boolean value, @Share("original") LocalBooleanRef vanilla) {
		return vanilla.get();
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5F"))
	private float getCriticalDamageMultiplier(float original, Entity target, @Share("original") LocalBooleanRef vanilla) {
		boolean vanillaCritical = vanilla.get();
		CriticalHitEvent hitResult = new CriticalHitEvent((Player) (Object) this, target, original, vanillaCritical);
		hitResult.sendEvent();
		if (hitResult.getResult() == BaseEvent.Result.ALLOW || (vanillaCritical && hitResult.getResult() == BaseEvent.Result.DEFAULT)) {
			vanilla.set(true);
			return hitResult.getDamageModifier();
		}
		return 1.0F;
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void playerAttackEntityEvent(Entity target, CallbackInfo ci) {
		AttackEntityEvent event = new AttackEntityEvent((Player) (Object) this, target);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}

	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	private void onLivingDeath(DamageSource cause, CallbackInfo ci) {
		LivingDeathEvent event = new LivingDeathEvent(this, cause);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), index = 2)
	private float livingHurtEvent(float value, DamageSource pDamageSource, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
		LivingHurtEvent event = new LivingHurtEvent(this, pDamageSource, value);
		eventRef.set(event);
		event.sendEvent();
		if (event.isCanceled())
			return 0;
		return event.getAmount();
	}

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
	private void shouldCancelHurt(DamageSource damageSource, float f, CallbackInfo ci, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
		if (eventRef.get().getAmount() <= 0)
			ci.cancel();
	}

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 5), index = 2)
	private float livingDamageEvent(float value, DamageSource pDamageSource) {
		LivingDamageEvent event = new LivingDamageEvent(this, pDamageSource, value);
		event.sendEvent();
		if (event.isCanceled())
			return 0;
		return event.getAmount();
	}

	private final ThreadLocal<BlockPos> pl$destroySpeedContext = new ThreadLocal<>();

	@Override
	public void setDigSpeedContext(@Nullable BlockPos pos) {
		pl$destroySpeedContext.set(pos);
	}

	@ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
	private float breakspeedEvent(float original, BlockState state) {
		PlayerEvents.BreakSpeed breakSpeed = new PlayerEvents.BreakSpeed((Player) (Object) this, state, original, pl$destroySpeedContext.get());
		breakSpeed.sendEvent();
		pl$destroySpeedContext.remove();
		return breakSpeed.getNewSpeed();
	}
}
