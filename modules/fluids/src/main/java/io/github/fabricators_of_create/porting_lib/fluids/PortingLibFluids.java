package io.github.fabricators_of_create.porting_lib.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

public class PortingLibFluids implements ModInitializer {
	public static final Codec<Long> POSITIVE_LONG = longRangeWithMessage(1, Long.MAX_VALUE, (integer) -> {
		return "Value must be positive: " + integer;
	});
	public static final ResourceKey<Registry<FluidType>> FLUID_TYPE_REGISTRY = PortingLib.key("fluid_type");
	public static final Registry<FluidType> FLUID_TYPES = FabricRegistryBuilder.createDefaulted(FLUID_TYPE_REGISTRY, PortingLib.id("empty")).buildAndRegister();

	@Override
	public void onInitialize() {
//		Registry.register(FLUID_TYPES, PortingLib.id("empty"), EMPTY_TYPE);
//		Registry.register(FLUID_TYPES, PortingLib.id("water"), WATER_TYPE);
//		Registry.register(FLUID_TYPES, PortingLib.id("lava"), LAVA_TYPE);
	}

	private static Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> messageFunction) {
		return Codec.LONG.validate((value) -> {
			return value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
				return messageFunction.apply(value);
			});
		});
	}
}
