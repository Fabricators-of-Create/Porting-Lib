package io.github.fabricators_of_create.porting_lib.item.api.extensions;

import io.github.fabricators_of_create.porting_lib.item.api.itemgroup.PortingLibCreativeTab;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import java.util.List;

public interface CreativeModeTabExt {
	void setPortingData(PortingLibCreativeTab.TabData data);

	@Nullable
	PortingLibCreativeTab.TabData getPortingTabData();
}
