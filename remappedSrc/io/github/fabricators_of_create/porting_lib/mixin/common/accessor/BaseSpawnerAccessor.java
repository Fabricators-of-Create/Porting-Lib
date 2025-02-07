package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.util.collection.DataPool;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobSpawnerLogic.class)
public interface BaseSpawnerAccessor {
	@Accessor("spawnPotentials")
	DataPool<MobSpawnerEntry> port_lib$getSpawnPotentials();

	@Accessor("nextSpawnData")
	MobSpawnerEntry port_lib$getNextSpawnData();
}
