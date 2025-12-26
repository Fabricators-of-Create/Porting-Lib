package io.github.fabricators_of_create.porting_lib.world;

import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttribute;

public class PortingLibEnvironmentAttributes {
	public static Identifier DEFAULT_CUSTOM_CLOUDS = Identifier.withDefaultNamespace("default");
	public static EnvironmentAttribute<Identifier> CUSTOM_CLOUDS = EnvironmentAttribute.builder(PortingLibAttributeTypes.IDENTIFIER)
			.defaultValue(DEFAULT_CUSTOM_CLOUDS)
			.syncable()
			.build();
	public static Identifier DEFAULT_CUSTOM_SKYBOX = Identifier.withDefaultNamespace("default");
	public static EnvironmentAttribute<Identifier> CUSTOM_SKYBOX = EnvironmentAttribute.builder(PortingLibAttributeTypes.IDENTIFIER)
			.defaultValue(DEFAULT_CUSTOM_SKYBOX)
			.syncable()
			.build();
	public static Identifier DEFAULT_CUSTOM_WEATHER_EFFECTS = Identifier.withDefaultNamespace("default");
	public static EnvironmentAttribute<Identifier> CUSTOM_WEATHER_EFFECTS = EnvironmentAttribute.builder(PortingLibAttributeTypes.IDENTIFIER)
			.defaultValue(DEFAULT_CUSTOM_WEATHER_EFFECTS)
			.syncable()
			.build();
}
