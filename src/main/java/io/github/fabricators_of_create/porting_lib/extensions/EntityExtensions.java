package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;

public interface EntityExtensions {
	default CompoundTag getExtraCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * If a rider of this entity can interact with this entity. Should return true on the
	 * ridden entity if so.
	 *
	 * @return if the entity can be interacted with from a rider
	 */
	default boolean canRiderInteract() {
		return false;
	}

	default Entity changeDimension(ServerLevel p_20118_, ITeleporter teleporter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

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
