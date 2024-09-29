package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.events.BlockToolModificationEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class PortingHooks {
	public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
		if (!state.requiresCorrectToolForDrops())
			return true;

		return player.hasCorrectToolForDrops(state);
	}

	/**
	 * @return -1 to cancel, MIN_VALUE to follow vanilla logic, any other number to modify granted exp
	 */
	public static int onGrindstoneChange(ItemStack top, ItemStack bottom, Container outputSlot, int xp) {
		GrindstoneEvent.OnPlaceItem e = new GrindstoneEvent.OnPlaceItem(top, bottom, xp);
		e.sendEvent();
		if (e.isCanceled()) {
			outputSlot.setItem(0, ItemStack.EMPTY);
			return -1;
		}
		if (e.getOutput().isEmpty())
			return Integer.MIN_VALUE;

		outputSlot.setItem(0, e.getOutput());
		return e.getXp();
	}

	public static boolean onGrindstoneTake(Container inputSlots, ContainerLevelAccess access, Function<Level, Integer> xpFunction) {
		access.execute((l, p) -> {
			int xp = xpFunction.apply(l);
			GrindstoneEvent.OnTakeItem e = new GrindstoneEvent.OnTakeItem(inputSlots.getItem(0), inputSlots.getItem(1), xp);
			e.sendEvent();
			if (e.isCanceled()) {
				return;
			}
			if (l instanceof ServerLevel) {
				ExperienceOrb.award((ServerLevel) l, Vec3.atCenterOf(p), e.getXp());
			}
			l.levelEvent(LevelEvent.SOUND_GRINDSTONE_USED, p, 0);
			inputSlots.setItem(0, e.getNewTopItem());
			inputSlots.setItem(1, e.getNewBottomItem());
			inputSlots.setChanged();
		});
		return true;
	}

	@Nullable
	public static BlockState onToolUse(BlockState originalState, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		BlockToolModificationEvent event = new BlockToolModificationEvent(originalState, context, itemAbility, simulate);
		return event.post() ? null : event.getFinalState();
	}
}
