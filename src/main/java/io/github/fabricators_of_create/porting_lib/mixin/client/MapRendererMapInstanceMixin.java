package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.render.MapDecorationIterator;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.MapRenderer;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(MapRenderer.MapInstance.class)
public abstract class MapRendererMapInstanceMixin {

	@Unique
	private final AtomicInteger cachedIndex = new AtomicInteger(0);

	@ModifyVariable(
			method = "draw",
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;",
					shift = At.Shift.BY,
					by = 2
			)
	)
	private Iterator<MapDecoration> wrapIterator(Iterator<MapDecoration> iterator) {
		return new MapDecorationIterator(iterator, cachedIndex);
	}

	@Inject(
			method = "draw",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;hasNext()Z",
					shift = At.Shift.BY,
					by = 3
			)
	)
	private void getCurrentIndex(CallbackInfo ci, @Local(ordinal = 3) int k) {
		this.cachedIndex.set(k);
	}

	@ModifyVariable(method = "draw", at = @At(value = "LOAD", ordinal = 0), index = 10)
	private int updateIndex(int oldK) {
		return this.cachedIndex.get();
	}
}
