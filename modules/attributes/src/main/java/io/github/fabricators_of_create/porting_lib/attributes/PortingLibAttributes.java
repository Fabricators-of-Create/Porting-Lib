package io.github.fabricators_of_create.porting_lib.attributes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import io.github.fabricators_of_create.porting_lib.attributes.extensions.PlayerAttributesExtensions;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.phys.Vec3;

public class PortingLibAttributes implements ModInitializer {
	/**
	 * Reach Distance represents the distance at which a player may interact with the world.  The default is 4.5 blocks.  Players in creative mode have an additional 0.5 blocks of block reach.
	 * @see PlayerAttributesExtensions#getBlockReach()
	 * @see PlayerAttributesExtensions#canReach(BlockPos, double)
	 */
	public static final Holder<Attribute> BLOCK_REACH = Holder.direct(ReachEntityAttributes.REACH);

	public static final Holder<Attribute> SWIM_SPEED = register("swim_speed", new RangedAttribute("porting_lib.swim_speed", 1.0D, 0.0D, 1024.0D).setSyncable(true));

	/**
	 * Attack Range represents the distance at which a player may attack an entity.  The default is 3 blocks.  Players in creative mode have an additional 3 blocks of entity reach.
	 * The default of 3.0 is technically considered a bug by Mojang - see MC-172289 and MC-92484. However, updating this value would allow for longer-range attacks on vanilla servers, which makes some people mad.
	 * @see PlayerAttributesExtensions#getEntityReach()
	 * @see PlayerAttributesExtensions#canReach(Entity, double)
	 * @see PlayerAttributesExtensions#canReach(Vec3, double)
	 */
	public static final Holder<Attribute> ENTITY_REACH = Holder.direct(ReachEntityAttributes.ATTACK_RANGE);

	public static Holder<Attribute> register(String id, Attribute attribute) {
		return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, PortingLib.id(id), attribute);
	}

	@Override
	public void onInitialize() {}
}
