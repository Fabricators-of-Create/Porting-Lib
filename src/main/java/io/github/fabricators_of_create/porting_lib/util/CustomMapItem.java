package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;

import static net.minecraft.world.item.MapItem.getMapId;
import static net.minecraft.world.item.MapItem.getSavedData;

public interface CustomMapItem {
	@Nullable
	default MapItemSavedData getCustomMapData(ItemStack p_42910_, Level p_42911_) {
		Integer integer = getMapId(p_42910_);
		return getSavedData(integer, p_42911_);
	}
}
