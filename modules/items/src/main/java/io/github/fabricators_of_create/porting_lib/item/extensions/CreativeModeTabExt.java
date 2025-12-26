package io.github.fabricators_of_create.porting_lib.item.extensions;

import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab.TabData;

import org.jspecify.annotations.Nullable;

public interface CreativeModeTabExt {
	void setPortingData(TabData data);

	@Nullable
	TabData getPortingTabData();
}
