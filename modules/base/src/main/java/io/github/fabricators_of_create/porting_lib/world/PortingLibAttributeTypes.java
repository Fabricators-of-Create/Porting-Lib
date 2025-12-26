package io.github.fabricators_of_create.porting_lib.world;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;

public class PortingLibAttributeTypes {
	/**
	 * Allows {@link Identifier} to be used as the type of {@link EnvironmentAttribute}.
	 */
	public static final AttributeType<Identifier> IDENTIFIER = Registry.register(BuiltInRegistries.ATTRIBUTE_TYPE, PortingLib.id("identifier"), AttributeType.ofNotInterpolated(Identifier.CODEC));

	public static void init() {}
}
