package io.github.fabricators_of_create.porting_lib.attributes;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class PortingLibAttributes {
	public static final Attribute STEP_HEIGHT_ADDITION = new RangedAttribute("porting_lib.stepHeight", 0.0D, -512.0D, 512.0D).setSyncable(true);
	public static final Attribute ENTITY_GRAVITY = new RangedAttribute("porting_lib.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true);
	public static final Attribute SWIM_SPEED = new RangedAttribute("forge.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true);

	public static void init() {
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("step_height_addition"), STEP_HEIGHT_ADDITION);
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("entity_gravity"), ENTITY_GRAVITY);
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("swim_speed"), SWIM_SPEED);
	}
}
