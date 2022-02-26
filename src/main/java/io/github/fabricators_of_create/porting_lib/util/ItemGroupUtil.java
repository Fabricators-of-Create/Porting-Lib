package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;

public class ItemGroupUtil {
	public static synchronized int getGroupCountSafe() {
		((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
		return CreativeModeTab.TABS.length - 1;
	}
}
