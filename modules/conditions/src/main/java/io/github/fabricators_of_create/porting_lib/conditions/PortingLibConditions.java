package io.github.fabricators_of_create.porting_lib.conditions;

import com.mojang.serialization.MapCodec;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class PortingLibConditions {
	public static final ResourceKey<Registry<MapCodec<? extends ICondition>>> CONDITION_CODECS_KEY =  ResourceKey.createRegistryKey(PortingLib.id("condition_codecs"));
	public static final Registry<MapCodec<? extends ICondition>> CONDITION_SERIALIZERS = FabricRegistryBuilder.createSimple(CONDITION_CODECS_KEY).buildAndRegister();

	public static void init() {}
}
