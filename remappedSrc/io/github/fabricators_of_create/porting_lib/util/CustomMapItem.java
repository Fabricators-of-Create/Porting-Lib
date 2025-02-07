package io.github.fabricators_of_create.porting_lib.util;

import org.jetbrains.annotations.Nullable;

import static net.minecraft.item.FilledMapItem.getMapId;
import static net.minecraft.item.FilledMapItem.getOrCreateMapState;

import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;

public interface CustomMapItem {
	@Nullable
	default MapState getCustomMapData(ItemStack p_42910_, World p_42911_) {
		Integer integer = getMapId(p_42910_);
		return getMapState(integer, p_42911_);
	}
}
