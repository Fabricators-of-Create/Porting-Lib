package io.github.fabricators_of_create.porting_lib.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(PortingLibLoot.class)
public class PortingLibLootMixin {
	@ModifyReturnValue(method = "getLootingLevel", at = @At("RETURN"))
	private static int modifyLootingLevel(int value, Entity target, @Nullable Entity killer, @Nullable DamageSource cause) {
		if (target instanceof LivingEntity living)
			return LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(cause, living, value, false);
		return value;
	}
}
