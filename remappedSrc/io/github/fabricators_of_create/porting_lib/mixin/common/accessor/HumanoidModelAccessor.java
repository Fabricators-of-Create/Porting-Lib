package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BipedEntityModel.class)
public interface HumanoidModelAccessor {
	@Invoker("poseRightArm")
	void port_lib$poseRightArm(LivingEntity livingEntity);

	@Invoker("poseLeftArm")
	void port_lib$poseLeftArm(LivingEntity livingEntity);
}
