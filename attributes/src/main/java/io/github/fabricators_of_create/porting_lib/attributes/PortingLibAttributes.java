package io.github.fabricators_of_create.porting_lib.attributes;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class PortingLibAttributes implements ModInitializer {
	public static final Attribute STEP_HEIGHT_ADDITION = new RangedAttribute("porting_lib.stepHeight", 0.0D, -512.0D, 512.0D).setSyncable(true);
	public static final Attribute ENTITY_GRAVITY = new RangedAttribute("porting_lib.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true);
	public static final Attribute SWIM_SPEED = new RangedAttribute("porting_lib.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true);

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingConstants.id("step_height_addition"), STEP_HEIGHT_ADDITION);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingConstants.id("entity_gravity"), ENTITY_GRAVITY);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingConstants.id("swim_speed"), SWIM_SPEED);
	}
}
