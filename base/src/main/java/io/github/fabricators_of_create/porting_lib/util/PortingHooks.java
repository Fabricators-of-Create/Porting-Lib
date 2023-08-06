package io.github.fabricators_of_create.porting_lib.util;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockItemExtensions;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("UnstableApiUsage")
public class PortingHooks {
	public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
		if (!state.requiresCorrectToolForDrops())
			return true;

		return player.hasCorrectToolForDrops(state);
	}

	public static void init() {
		RegistryEntryRemovedCallback.event(BuiltInRegistries.ITEM).register((rawId, id, item) -> {
			if (item instanceof BlockItemExtensions blockItem) {
				blockItem.removeFromBlockToItemMap(Item.BY_BLOCK, item);
			}
		});
	}

	public static int onGrindstoneChange(@NotNull ItemStack top, @NotNull ItemStack bottom, Container outputSlot, int xp) {
		GrindstoneEvents.OnplaceItem e = new GrindstoneEvents.OnplaceItem(top, bottom, xp);
		if (e.isCanceled()) {
			outputSlot.setItem(0, ItemStack.EMPTY);
			return -1;
		}
		if (e.getOutput().isEmpty()) return Integer.MIN_VALUE;

		outputSlot.setItem(0, e.getOutput());
		return e.getXp();
	}
}
