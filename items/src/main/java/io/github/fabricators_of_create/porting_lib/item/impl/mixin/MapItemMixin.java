package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.CustomMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapItem.class)
public abstract class MapItemMixin {
	@Inject(
			method = "getSavedData(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void port_lib$customMapData(ItemStack stack, Level level, CallbackInfoReturnable<MapItemSavedData> cir) {
		if (stack.getItem() instanceof CustomMapItem customMapItem) {
			cir.setReturnValue(customMapItem.getCustomMapData(stack, level));
		}
	}
}
