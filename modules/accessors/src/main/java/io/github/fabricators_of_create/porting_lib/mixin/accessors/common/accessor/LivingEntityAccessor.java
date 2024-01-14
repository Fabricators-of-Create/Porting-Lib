package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.core.BlockPos;

import net.minecraft.sounds.SoundEvent;

import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
	@Accessor("jumping")
	boolean port_lib$isJumping();

	@Accessor("lastPos")
	BlockPos port_lib$lastPos();

	@Invoker("spawnItemParticles")
	void port_lib$spawnItemParticles(ItemStack stack, int count);

	@Invoker("getDeathSound")
	SoundEvent port_lib$getDeathSound();

	@Invoker("onEffectRemoved")
	void port_lib$onEffectRemoved(MobEffectInstance effect);
}
