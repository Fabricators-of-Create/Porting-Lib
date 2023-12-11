package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityStruckByLightningEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {
	@WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
	private boolean shouldStrikeEntity(Entity entity, ServerLevel level, LightningBolt lightningBolt) {
		var event = new EntityStruckByLightningEvent(entity, lightningBolt);
		event.setCanceled(EntityEvents.STRUCK_BY_LIGHTING.invoker().onEntityStruckByLightning(entity, lightningBolt));
		event.sendEvent();
		return !event.isCanceled();
	}
}
