package io.github.fabricators_of_create.porting_lib.models.generators;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;

@VisibleForTesting
public interface IGeneratedBlockState {
	JsonObject toJson();
}
