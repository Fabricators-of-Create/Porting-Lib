package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

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
	void port_lib$onEffectRemoved(StatusEffectInstance effect);
}
