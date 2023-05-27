package io.github.fabricators_of_create.porting_lib.attributes;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class PortingLibAttributes implements ModInitializer {
	public static final Attribute STEP_HEIGHT_ADDITION = new RangedAttribute("porting_lib.stepHeight", 0.0D, -512.0D, 512.0D).setSyncable(true);
	public static final Attribute REACH_DISTANCE = new RangedAttribute("generic.reachDistance", 4.5D, 0.0D, 1024.0D).setSyncable(true);
	public static final Attribute ENTITY_GRAVITY = new RangedAttribute("porting_lib.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true);
	public static final Attribute SWIM_SPEED = new RangedAttribute("porting_lib.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true);

	/**
	 * Attack Range represents the distance at which a player may attack an entity.  The default is 3 blocks.  Players in creative mode have an additional 3 blocks of attack reach.
	 * @see IForgePlayer#getAttackRange()
	 * @see IForgePlayer#canHit(Entity, double)
	 */
	public static final Attribute ATTACK_RANGE = new RangedAttribute("generic.attack_range", 3.0D, 0.0D, 1024.0D).setSyncable(true);

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("step_height_addition"), STEP_HEIGHT_ADDITION);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("entity_gravity"), ENTITY_GRAVITY);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("swim_speed"), SWIM_SPEED);

		if (!FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) {
			Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("reach_distance"), REACH_DISTANCE);
			Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("attack_range"), ATTACK_RANGE);
		}
	}
}
