package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Invoker
	float callGetEyeHeight(Pose pose, EntityDimensions dimensions);
}
