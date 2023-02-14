package io.github.fabricators_of_create.porting_lib.data.extensions;

import net.minecraft.client.main.GameConfig;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface MinecraftExtension {
	GameConfig port_lib$getGameConfig();
}
