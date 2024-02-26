package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.ShieldBlockItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

	@Shadow
	public abstract void disableShield(boolean sprinting);

	protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
		super(entityType, level);
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

	@Inject(method = "createAttributes", at = @At("RETURN"))
	private static void port_lib$addKnockback(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
		cir.getReturnValue().add(Attributes.ATTACK_KNOCKBACK);
	}
}
