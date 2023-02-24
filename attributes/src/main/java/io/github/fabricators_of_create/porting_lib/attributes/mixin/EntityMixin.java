package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

@Mixin(Entity.class)
public class EntityMixin {

	@ModifyReturnValue(method = "maxUpStep", at = @At("RETURN"))
	private float modifyStepHeight(float vanillaStep) {
		if ((Object) this instanceof LivingEntity living) {
			AttributeInstance stepHeightAttribute = living.getAttribute(PortingLibAttributes.STEP_HEIGHT_ADDITION);
			if (stepHeightAttribute != null) {
				return (float) Math.max(0, vanillaStep + stepHeightAttribute.getValue());
			}
		}
		return vanillaStep;
	}
}
