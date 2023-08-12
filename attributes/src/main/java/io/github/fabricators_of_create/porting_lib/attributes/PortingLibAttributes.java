package io.github.fabricators_of_create.porting_lib.attributes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import io.github.fabricators_of_create.porting_lib.attributes.extensions.PlayerAttributesExtensions;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.phys.Vec3;

public class PortingLibAttributes implements ModInitializer {
	public static final Attribute STEP_HEIGHT_ADDITION = new RangedAttribute("porting_lib.stepHeight", 0.0D, -512.0D, 512.0D).setSyncable(true);
	/**
	 * Reach Distance represents the distance at which a player may interact with the world.  The default is 4.5 blocks.  Players in creative mode have an additional 0.5 blocks of block reach.
	 * @see PlayerAttributesExtensions#getBlockReach()
	 * @see PlayerAttributesExtensions#canReach(BlockPos, double)
	 */
	public static final Attribute BLOCK_REACH = ReachEntityAttributes.REACH;

	public static final Attribute ENTITY_GRAVITY = new RangedAttribute("porting_lib.entity_gravity", 0.08D, -8.0D, 8.0D).setSyncable(true);
	public static final Attribute SWIM_SPEED = new RangedAttribute("porting_lib.swimSpeed", 1.0D, 0.0D, 1024.0D).setSyncable(true);

	/**
	 * Attack Range represents the distance at which a player may attack an entity.  The default is 3 blocks.  Players in creative mode have an additional 3 blocks of entity reach.
	 * The default of 3.0 is technically considered a bug by Mojang - see MC-172289 and MC-92484. However, updating this value would allow for longer-range attacks on vanilla servers, which makes some people mad.
	 * @see PlayerAttributesExtensions#getEntityReach()
	 * @see PlayerAttributesExtensions#canReach(Entity, double)
	 * @see PlayerAttributesExtensions#canReach(Vec3, double)
	 */
	public static final Attribute ENTITY_REACH = ReachEntityAttributes.ATTACK_RANGE;

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("step_height_addition"), STEP_HEIGHT_ADDITION);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("entity_gravity"), ENTITY_GRAVITY);
		Registry.register(BuiltInRegistries.ATTRIBUTE, PortingLib.id("swim_speed"), SWIM_SPEED);
	}
}
