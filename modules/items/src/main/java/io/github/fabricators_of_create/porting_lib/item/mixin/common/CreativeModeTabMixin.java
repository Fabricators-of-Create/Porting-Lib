package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.item.extensions.CreativeModeTabExt;
import io.github.fabricators_of_create.porting_lib.item.itemgroup.PortingLibCreativeTab.TabData;
import net.minecraft.world.item.CreativeModeTab;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements CreativeModeTabExt {
	private @Nullable TabData porting$data;

	@Override
	public void setPortingData(TabData data) {
		this.porting$data = data;
	}

	@Override
	public TabData getPortingTabData() {
		return this.porting$data;
	}
}
