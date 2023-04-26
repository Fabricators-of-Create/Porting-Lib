package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.event.common.MobEntitySetTargetCallback;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
	public MobMixin() {
		super(null, null);
	}

	@Inject(method = "setTarget", at = @At("TAIL"))
	private void port_lib$setTarget(LivingEntity target, CallbackInfo ci) {
		MobEntitySetTargetCallback.EVENT.invoker().onMobEntitySetTarget((Mob) (Object) this, target);
	}

	@WrapOperation(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getNearestPlayer(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/entity/player/Player;"))
	private Player despawnEvent(Level level, Entity entity, double maxDistance, Operation<Player> operation) {
		BaseEvent.Result result = PortingHooks.canEntityDespawn(MixinHelper.cast(this), (ServerLevel) this.level);
		if (result == BaseEvent.Result.DENY) {
			noActionTime = 0;
			return null;
		} else if (result == BaseEvent.Result.ALLOW) {
			this.discard();
			return null;
		} else {
			return operation.call(level, entity, maxDistance);
		}
	}
}
