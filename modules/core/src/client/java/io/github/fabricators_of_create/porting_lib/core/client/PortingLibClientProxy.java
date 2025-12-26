package io.github.fabricators_of_create.porting_lib.core.client;

import io.github.fabricators_of_create.porting_lib.core.util.PortingLibProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.TooltipFlag;

import org.jspecify.annotations.Nullable;

public class PortingLibClientProxy extends PortingLibProxy {
	@Override
	public BlockableEventLoop<Runnable> getClientExecutor() {
		return Minecraft.getInstance();
	}

	@Override
	public TooltipFlag getTooltipFlag() {
		return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
	}

	@Override
	@Nullable
	public <T> HolderLookup.RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
		var lookup = super.resolveLookup(key);
		if (lookup == null) {
			ClientLevel level = Minecraft.getInstance().level;
			if (level != null) {
				return level.registryAccess().lookup(key).orElse(null);
			}
		}
		return lookup;
	}
}
