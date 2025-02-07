
package io.github.fabricators_of_create.porting_lib.util;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.util.Identifier;

public class TrueCondition implements ConditionJsonProvider {
	public static final Identifier ID = PortingLib.id("true");
	public static final TrueCondition INSTANCE = new TrueCondition();

	public static void init() {
		ResourceConditions.register(ID, jsonObject -> true);
	}

	@Override
	public Identifier getConditionId() {
		return ID;
	}

	@Override
	public void writeParameters(JsonObject object) {
	}
}
