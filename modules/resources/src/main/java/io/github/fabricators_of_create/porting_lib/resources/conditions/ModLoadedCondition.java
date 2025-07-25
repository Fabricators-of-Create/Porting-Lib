package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.loader.api.FabricLoader;

public record ModLoadedCondition(String modId) implements ICondition {
	public static MapCodec<ModLoadedCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			Codec.STRING.fieldOf("modid")
				.forGetter(ModLoadedCondition::modId)
		)
			.apply(instance, ModLoadedCondition::new)
	);

	@Override
	public boolean test(IContext context) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
}
