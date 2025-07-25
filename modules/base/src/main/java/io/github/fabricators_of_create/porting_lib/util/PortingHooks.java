package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.core.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;

import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class PortingHooks {
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

	/**
	 * Attempts to resolve a {@link HolderLookup.RegistryLookup} using the current global state.
	 * <p>
	 * Prioritizes the server's lookup, only attempting to retrieve it from the client if the server is unavailable.
	 *
	 * @param <T> The type of registry being looked up
	 * @param key The resource key for the target registry
	 * @return A registry access, if one was available.
	 */
	@Nullable
	public static <T> HolderLookup.RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			return server.registryAccess().lookup(key).orElse(null);
		} else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return resolveLookupClient(key);
		}

		return null;
	}

	@Nullable
	public static <T> HolderLookup.RegistryLookup<T> resolveLookupClient(ResourceKey<? extends Registry<T>> key) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null) {
			return level.registryAccess().lookup(key).orElse(null);
		}

		return null;
	}
}
