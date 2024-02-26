package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("storageSource")
	LevelStorageAccess port_lib$getStorageSource();

	@Accessor("resources")
	MinecraftServer.ReloadableResources port_lib$getServerResources();
}
