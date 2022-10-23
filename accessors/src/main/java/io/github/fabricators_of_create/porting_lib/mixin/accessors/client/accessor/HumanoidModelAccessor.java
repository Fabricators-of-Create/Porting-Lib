package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import net.minecraft.client.model.HumanoidModel;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidModel.class)
public interface HumanoidModelAccessor {
	@Invoker("poseRightArm")
	void port_lib$poseRightArm(LivingEntity livingEntity);

	@Invoker("poseLeftArm")
	void port_lib$poseLeftArm(LivingEntity livingEntity);
}
