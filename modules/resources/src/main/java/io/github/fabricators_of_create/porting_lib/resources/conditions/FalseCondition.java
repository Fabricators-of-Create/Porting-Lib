package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.mojang.serialization.MapCodec;

public final class FalseCondition implements ICondition {
	public static final FalseCondition INSTANCE = new FalseCondition();

	public static final MapCodec<FalseCondition> CODEC = MapCodec.unit(INSTANCE).stable();

	private FalseCondition() {}

	@Override
	public boolean test(IContext condition) {
		return false;
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	public String toString() {
		return "false";
	}
}
