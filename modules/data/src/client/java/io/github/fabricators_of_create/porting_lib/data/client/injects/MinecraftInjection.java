package io.github.fabricators_of_create.porting_lib.data.client.injects;

import net.minecraft.client.main.GameConfig;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface MinecraftInjection {
	GameConfig port_lib$getGameConfig();
}
