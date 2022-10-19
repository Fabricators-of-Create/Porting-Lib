package io.github.fabricators_of_create.porting_lib.attributes.extensions;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public interface EntityAttributes {
	/**
	 * @return Return the height in blocks the Entity can step up without needing to jump
	 * This is the sum of vanilla's {@link Entity#maxUpStep} field and the current value
	 * of the {@link PortingLibAttributes#STEP_HEIGHT_ADDITION} attribute
	 * (if this Entity is a {@link LivingEntity} and has the attribute), clamped at 0.
	 */
	default float getStepHeight() {
		float vanillaStep = ((Entity)this).maxUpStep;
		if (this instanceof LivingEntity living) {
			AttributeInstance stepHeightAttribute = living.getAttribute(PortingLibAttributes.STEP_HEIGHT_ADDITION);
			if (stepHeightAttribute != null) {
				return (float) Math.max(0, vanillaStep + stepHeightAttribute.getValue());
			}
		}
		return vanillaStep;
	}
}
