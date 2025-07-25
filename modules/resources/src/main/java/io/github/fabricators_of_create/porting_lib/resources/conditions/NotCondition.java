package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record NotCondition(ICondition value) implements ICondition {
	public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			ICondition.CODEC.fieldOf("value")
				.forGetter(NotCondition::value)
		)
			.apply(instance, NotCondition::new)
	);

	@Override
	public boolean test(IContext context) {
		return !value.test(context);
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
}
