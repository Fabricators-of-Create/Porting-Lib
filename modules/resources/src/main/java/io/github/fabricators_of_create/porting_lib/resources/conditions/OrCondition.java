package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record OrCondition(List<ICondition> values) implements ICondition {
	public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
					.group(
							LIST_CODEC.fieldOf("values").forGetter(OrCondition::values))
					.apply(builder, OrCondition::new));

	@Override
	public boolean test(IContext context) {
		for (ICondition child : values()) {
			if (child.test(context))
				return true;
		}

		return false;
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
}
