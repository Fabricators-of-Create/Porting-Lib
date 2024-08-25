package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.player.CriticalHitEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.AttackEntityEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerXpEvent;
import io.github.fabricators_of_create.porting_lib.entity.ext.PlayerExt;
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
public abstract class PlayerMixin extends LivingEntity implements PlayerExt {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void playerStartTickEvent(CallbackInfo ci) {
		EntityHooks.firePlayerTickPre(MixinHelper.cast(this));
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void playerEndTickEvent(CallbackInfo ci) {
		EntityHooks.firePlayerTickPost(MixinHelper.cast(this));
	}

	@Inject(method = "interactOn", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;",
			ordinal = 0
	), cancellable = true)
	public void onEntityInteract(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult cancelResult = EntityHooks.onInteractEntity(MixinHelper.cast(this), entityToInteractOn, hand);
		if (cancelResult != null) cir.setReturnValue(cancelResult);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	public void onPlayerAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!EntityHooks.onPlayerAttack(this, source, amount))
			cir.setReturnValue(false);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), index = 1, argsOnly = true)
	private int xpChange(int experience, @Share("event") LocalRef<PlayerXpEvent.XpChange> eventRef) {
		PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange((Player) (Object) this, experience);
		eventRef.set(event);
		event.sendEvent();
		return event.getAmount();
	}

	@Inject(method = "giveExperiencePoints", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;increaseScore(I)V"), cancellable = true)
	private void cancelPoints(int experience, CallbackInfo ci, @Share("event") LocalRef<PlayerXpEvent.XpChange> eventRef) {
		if (eventRef.get().isCanceled())
			ci.cancel();
	}

	@ModifyVariable(method = "giveExperienceLevels", at = @At("HEAD"), index = 1, argsOnly = true)
	private int levelChange(int level, @Share("event") LocalRef<PlayerXpEvent.LevelChange> eventRef) {
		PlayerXpEvent.LevelChange event = new PlayerXpEvent.LevelChange((Player) (Object) this, level);
		eventRef.set(event);
		event.sendEvent();
		return event.getLevels();
	}

	@Inject(method = "giveExperienceLevels", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;experienceLevel:I", ordinal = 0), cancellable = true)
	private void cancelLevels(int levels, CallbackInfo ci, @Share("event") LocalRef<PlayerXpEvent.LevelChange> eventRef) {
		if (eventRef.get().isCanceled())
			ci.cancel();
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void onPlayerAttackTarget(Entity target, CallbackInfo ci) {
		if (!EntityHooks.onPlayerAttackTarget((Player) (Object) this, target))
			ci.cancel();
	}

	@ModifyVariable(
			method = "attack",
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1),
					to = @At(value = "CONSTANT", args = "floatValue=1.5F")
			),
			at = @At(value = "STORE", opcode = Opcodes.ISTORE), index = 9
	)
	private boolean modifyResult(boolean value, @Share("original") LocalBooleanRef vanilla) {
		vanilla.set(value);
		return true;
	}

	@ModifyVariable(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5F"), index = 9)
	private boolean modifyVanillaResult(boolean value, @Share("original") LocalBooleanRef vanilla) {
		return vanilla.get();
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5F"))
	private float getCriticalDamageMultiplier(float original, Entity target, @Share("original") LocalBooleanRef vanilla, @Share("event") LocalRef<CriticalHitEvent> eventRef) {
		boolean vanillaCritical = vanilla.get();
		var critEvent = EntityHooks.fireCriticalHit((Player) (Object) this, target, vanillaCritical, vanillaCritical ? original : 1.0F);
		eventRef.set(critEvent);
		if (critEvent.isCriticalHit()) {
			vanilla.set(true);
			return critEvent.getDamageMultiplier();
		}
		return 1.0F;
	}

	@ModifyVariable(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;walkDist:F"), index = 9)
	private boolean isCriticalHit(boolean value, @Share("event") LocalRef<CriticalHitEvent> eventRef) {
		return eventRef.get().isCriticalHit();
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
		if (EntityHooks.onLivingDeath(this, cause))
			ci.cancel();
	}

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), index = 2, argsOnly = true)
	private float livingHurtEvent(float amount, DamageSource pDamageSource) {
		return EntityHooks.onLivingHurt(this, pDamageSource, amount);
	}

	@Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
	private void shouldCancelHurt(DamageSource damageSource, float amount, CallbackInfo ci) {
		if (amount <= 0)
			ci.cancel();
	}

//	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 5), index = 2) TODO: PORT
//	private float livingDamageEvent(float value, DamageSource pDamageSource) {
//		LivingDamageEvent event = new LivingDamageEvent(this, pDamageSource, value);
//		event.sendEvent();
//		if (event.isCanceled())
//			return 0;
//		return event.getAmount();
//	}

	private final ThreadLocal<BlockPos> pl$destroySpeedContext = new ThreadLocal<>();

	@Override
	public void setDigSpeedContext(@Nullable BlockPos pos) {
		pl$destroySpeedContext.set(pos);
	}

	@ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
	private float breakspeedEvent(float original, BlockState state) {
		return EntityHooks.getBreakSpeed((Player) (Object) this, state, original, pl$destroySpeedContext.get());
	}
}
