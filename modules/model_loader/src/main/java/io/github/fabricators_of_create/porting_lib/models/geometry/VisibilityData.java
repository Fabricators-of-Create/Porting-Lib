package io.github.fabricators_of_create.porting_lib.models.geometry;

import java.util.HashMap;
import java.util.Map;

public class VisibilityData {
	private final Map<String, Boolean> data = new HashMap<>();

	public boolean hasCustomVisibility(String part) {
		return data.containsKey(part);
	}

	public boolean isVisible(String part, boolean fallback) {
		return data.getOrDefault(part, fallback);
	}

	public void setVisibilityState(String partName, boolean type) {
		data.put(partName, type);
	}

	public void copyFrom(VisibilityData visibilityData) {
		data.clear();
		data.putAll(visibilityData.data);
	}
}
