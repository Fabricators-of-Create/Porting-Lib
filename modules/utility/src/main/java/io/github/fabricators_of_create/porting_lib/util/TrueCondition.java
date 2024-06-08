
package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public class TrueCondition implements ResourceCondition {
	public static final ResourceLocation ID = PortingLib.id("true");
	public static final TrueCondition INSTANCE = new TrueCondition();
	public static final MapCodec<TrueCondition> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(TrueCondition::new));
	public static final ResourceConditionType<TrueCondition> conditionType = ResourceConditionType.create(ID, CODEC);

	public static void init() {
		ResourceConditions.register(conditionType);
	}

	@Override
	public ResourceConditionType<?> getType() {
		return conditionType;
	}

	@Override
	public boolean test(@Nullable HolderLookup.Provider registryLookup) {
		return true;
	}
}
