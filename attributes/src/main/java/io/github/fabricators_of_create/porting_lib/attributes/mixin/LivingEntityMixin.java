package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
	private static AttributeSupplier.Builder port_lib$addModdedAttributes(AttributeSupplier.Builder builder) {
		return builder.add(PortingLibAttributes.ENTITY_GRAVITY).add(PortingLibAttributes.SWIM_SPEED).add(PortingLibAttributes.STEP_HEIGHT_ADDITION);
	}

	@Shadow
	@Nullable
	public abstract AttributeInstance getAttribute(Attribute attribute);

	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	@ModifyVariable(method = "travel", at = @At(value = "STORE", ordinal = 0)) // double d = 0.08;
	private double port_lib$modifyGravity(double original) {
		if (original == 0.08) { // only apply gravity if other mods haven't changed it
			AttributeInstance attribute = this.getAttribute(PortingLibAttributes.ENTITY_GRAVITY);
			if (attribute != null)
				return attribute.getValue();
		}
		return original;
	}

	@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 0))
	private boolean port_lib$disableOldLogicFallingLogic(boolean original) {
		return false;
	}

	private static final AttributeModifier SLOW_FALLING = new AttributeModifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

	@Inject(
			method = "travel",
			at = @At(
					value = "CONSTANT",args = {
					"doubleValue=0.08D"
			}
			)
	)
	public void port_lib$entityGravity(Vec3 travelVector, CallbackInfo ci) {
		AttributeInstance gravity = this.getAttribute(PortingLibAttributes.ENTITY_GRAVITY);
		if (gravity != null) {
			boolean falling = this.getDeltaMovement().y <= 0.0D;
			if (falling && this.hasEffect(MobEffects.SLOW_FALLING)) {
				if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTransientModifier(SLOW_FALLING);
				this.resetFallDistance();
			} else if (gravity.hasModifier(SLOW_FALLING)) {
				((AttributeInstanceAccessor)gravity).callRemoveModifier(SLOW_FALLING);
			}
		}
	}

	@ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
	private float port_lib$swimSpeed(float original) {
		return original * (float)this.getAttribute(PortingLibAttributes.SWIM_SPEED).getValue();
	}

	@ModifyArg(method = "jumpInLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"), index = 1)
	private double port_lib$modifySwimSpeed(double y) {
		return y * this.getAttribute(PortingLibAttributes.SWIM_SPEED).getValue();
	}

	@ModifyReturnValue(method = "maxUpStep", at = @At("RETURN"))
	private float modifyStepHeight(float vanillaStep) {
		return (float) (vanillaStep + ((LivingEntity) (Object) this).getAttribute(PortingLibAttributes.STEP_HEIGHT_ADDITION).getValue());
	}
}
