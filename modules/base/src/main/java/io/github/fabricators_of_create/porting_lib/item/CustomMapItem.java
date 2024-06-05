package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;

import static net.minecraft.world.item.MapItem.getSavedData;

public interface CustomMapItem {
	@Nullable
	default MapItemSavedData getCustomMapData(ItemStack itemStack, Level level) {
		MapId mapId = itemStack.get(DataComponents.MAP_ID);
		return getSavedData(mapId, level);
	}
}
