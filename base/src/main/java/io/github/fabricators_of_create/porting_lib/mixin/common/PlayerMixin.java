package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.EntityInteractCallback;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;
import io.github.fabricators_of_create.porting_lib.item.ShieldBlockItem;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	@Shadow
	public abstract void disableShield(boolean sprinting);

	@Shadow
	public abstract Iterable<ItemStack> getArmorSlots();

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

	@ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
	private void port_lib$onHurt(Args args) {
		DamageSource source = args.get(0);
		float currentAmount = args.get(1);
		float newAmount = LivingEntityEvents.ACTUALLY_HURT.invoker().onHurt(source, this, currentAmount);
		args.set(1, newAmount);
	}

	@Inject(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
	public void port_lib$onEntityInteract(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult cancelResult = EntityInteractCallback.EVENT.invoker().onEntityInteract((Player) (Object) this, hand, entityToInteractOn);
		if (cancelResult != null) cir.setReturnValue(cancelResult);
	}

	@Inject(method = "blockUsingShield", at = @At("TAIL"))
	public void port_lib$blockShieldItem(LivingEntity entity, CallbackInfo ci) {
		if(entity.getMainHandItem().getItem() instanceof ShieldBlockItem shieldBlockItem) {
			if (shieldBlockItem.canDisableShield(entity.getMainHandItem(), this.useItem, this, entity))
				disableShield(true);
		}
	}

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemAttack(Entity targetEntity, CallbackInfo ci) {
		if(getMainHandItem().getItem().onLeftClickEntity(getMainHandItem(), (Player) (Object) this, targetEntity)) ci.cancel();
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	public void port_lib$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(LivingEntityEvents.ATTACK.invoker().onAttack(this, source, amount)) cir.setReturnValue(false);
	}

	@Inject(method = "createAttributes", at = @At("RETURN"))
	private static void port_lib$addKnockback(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
		cir.getReturnValue().add(Attributes.ATTACK_KNOCKBACK);
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float port_lib$onHurt(float amount, DamageSource source, float amount2) {
		return LivingEntityEvents.HURT.invoker().onHurt(source, amount);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"))
	private int port_lib$xpChange(int experience) {
		PlayerEvents.XpChange xpChange = new PlayerEvents.XpChange(MixinHelper.cast(this), experience);
		xpChange.sendEvent();
		return xpChange.getAmount();
	}
}
