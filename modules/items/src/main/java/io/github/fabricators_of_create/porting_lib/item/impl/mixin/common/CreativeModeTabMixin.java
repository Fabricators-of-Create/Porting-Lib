package io.github.fabricators_of_create.porting_lib.item.impl.mixin.common;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.item.api.extensions.CreativeModeTabExt;
import io.github.fabricators_of_create.porting_lib.item.api.itemgroup.PortingLibCreativeTab;
import net.minecraft.world.item.CreativeModeTab;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements CreativeModeTabExt {
	@Nullable
	private PortingLibCreativeTab.TabData porting$data;

	@Override
	public void setPortingData(PortingLibCreativeTab.TabData data) {
		this.porting$data = data;
	}

	@Override
	public PortingLibCreativeTab.TabData getPortingTabData() {
		return this.porting$data;
	}
}
