package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.entity.events.LightningStrikeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {
	@WrapOperation(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"
			)
	)
	private void fireStrikeEvents(Entity entity, ServerLevel level, LightningBolt self, Operation<Void> original) {
		if (LightningStrikeEvents.BEFORE.invoker().canLightningStrike(entity, self)) {
			original.call(entity, level, self);
			LightningStrikeEvents.AFTER.invoker().afterLightningStrike(entity, self);
		}
	}
}
