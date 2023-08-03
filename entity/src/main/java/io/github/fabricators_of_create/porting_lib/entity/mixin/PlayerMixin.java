package io.github.fabricators_of_create.porting_lib.entity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;

import io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable.WrapVariable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerExperienceEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerTickEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void playerTickStart(CallbackInfo ci) {
		PlayerTickEvents.START.invoker().onPlayerTickStart((Player) (Object) this);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void playerTickEnd(CallbackInfo ci) {
		PlayerTickEvents.END.invoker().onPlayerTickEnd((Player) (Object) this);
	}

	@Inject(
			method = "interactOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"
			),
			cancellable = true
	)
	public void onEntityInteract(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		InteractionResult result = PlayerEvents.USE_ENTITY.invoker().onUseEntity((Player) (Object) this, hand, entityToInteractOn);
		if (result != null)
			cir.setReturnValue(result);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true)
	private int onXpGrant(int experience) {
		return PlayerExperienceEvents.EXP_GRANT.invoker().modifyExpChange((Player) (Object) this, experience);
	}

	// make the knockback attribute apply to players - https://bugs.mojang.com/browse/MC-138868

	@ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
	private static AttributeSupplier.Builder addKnockbackAttribute(AttributeSupplier.Builder builder) {
		return builder.add(Attributes.ATTACK_KNOCKBACK);
	}

	@WrapVariable(
			method = "attack",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
					)
			),
			at = @At(
					value = "PORTING_LIB:WRAPPABLE",
					args = "opcodes=ILOAD",
					ordinal = 1 // first one is the boolean check
			)
	)
	private int triggerKnockbackCode(int knockback) {
		if (knockback == 0) {
			boolean hasKnockback = getAttributeValue(Attributes.ATTACK_KNOCKBACK) != 0;
			return hasKnockback ? 1 : 0;
		}
		return knockback;
	}

	@WrapVariable(
			method = "attack",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							ordinal = 0,
							args = {
									"intValue=0",
									"expandZeroConditions=GREATER_THAN_ZERO"
							}
					),
					to = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"
					)
			),
			at = @At(
					value = "PORTING_LIB:WRAPPABLE",
					args = "opcodes=I2F"
			),
			require = 3,
			allow = 3
	)
	private float addKnockbackAttribute(float knockback) {
		return knockback + (float) getAttributeValue(Attributes.ATTACK_KNOCKBACK);
	}
}
