package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityInteractCallback;
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
		InteractionResult result = EntityInteractCallback.EVENT.invoker().onEntityInteract((Player) (Object) this, hand, entityToInteractOn);
		if (result != null)
			cir.setReturnValue(result);
	}

	@Inject(method = "createAttributes", at = @At("RETURN"))
	private static void addKnockbackAttribute(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
		cir.getReturnValue().add(Attributes.ATTACK_KNOCKBACK);
	}

	@ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true)
	private int onXpGrant(int experience) {
		return PlayerExperienceEvents.EXP_GRANT.invoker().modifyExpChange((Player) (Object) this, experience);
	}
}
