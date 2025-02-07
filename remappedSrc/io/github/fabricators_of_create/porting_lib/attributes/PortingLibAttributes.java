package io.github.fabricators_of_create.porting_lib.attributes;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.registry.Registry;

public class PortingLibAttributes {
	public static final EntityAttribute STEP_HEIGHT_ADDITION = new ClampedEntityAttribute("porting_lib.stepHeight", 0.0D, -512.0D, 512.0D).setTracked(true);
	public static final EntityAttribute ENTITY_GRAVITY = new ClampedEntityAttribute("porting_lib.entity_gravity", 0.08D, -8.0D, 8.0D).setTracked(true);
	public static final EntityAttribute SWIM_SPEED = new ClampedEntityAttribute("forge.swimSpeed", 1.0D, 0.0D, 1024.0D).setTracked(true);

	public static void init() {
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("step_height_addition"), STEP_HEIGHT_ADDITION);
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("entity_gravity"), ENTITY_GRAVITY);
		Registry.register(Registry.ATTRIBUTE, PortingLib.id("swim_speed"), SWIM_SPEED);
	}
}
