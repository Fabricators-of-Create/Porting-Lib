package io.github.fabricators_of_create.porting_lib.common;

import io.github.fabricators_of_create.porting_lib.common.util.TrueCondition;
import net.fabricmc.api.ModInitializer;

public class PortingLibCommon implements ModInitializer {
	@Override
	public void onInitialize() {
		TrueCondition.init();
	}
}
