package io.github.fabricators_of_create.porting_lib.attributes;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class PortingLibAttributes implements ModInitializer {
	public static final Holder<Attribute> SWIM_SPEED = register("swim_speed", new RangedAttribute("porting_lib.swim_speed", 1.0D, 0.0D, 1024.0D).setSyncable(true));

	public static Holder<Attribute> register(String id, Attribute attribute) {
		return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, PortingLib.id(id), attribute);
	}

	@Override
	public void onInitialize() {}
}
