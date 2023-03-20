package io.github.fabricators_of_create.porting_lib.attributes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import net.minecraft.world.entity.ai.attributes.Attribute;

public class ReachEntityAttributesCompat {
	public static Attribute getReachAttribute() {
		return ReachEntityAttributes.REACH;
	}

	public static Attribute getAttackRange() {
		return ReachEntityAttributes.ATTACK_RANGE;
	}
}
