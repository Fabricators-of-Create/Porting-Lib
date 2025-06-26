package io.github.fabricators_of_create.porting_lib.item.extensions;

import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab;

import javax.annotation.Nullable;

public interface CreativeModeTabExt {
	void setPortingData(PortingLibCreativeTab.TabData data);

	@Nullable
	PortingLibCreativeTab.TabData getPortingTabData();
}
