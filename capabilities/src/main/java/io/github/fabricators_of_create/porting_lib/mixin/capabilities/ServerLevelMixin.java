package io.github.fabricators_of_create.porting_lib.mixin.capabilities;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.capabilities.impl.LevelCapabilityData;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.InitializableCapabilityExtension;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements InitializableCapabilityExtension {
	@Shadow
	public abstract DimensionDataStorage getDataStorage();

	@Unique
	private LevelCapabilityData capabilityData;

	protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
		super(levelData, dimension, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void port_lib$registerCapabilities(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimensionKey, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long seed, List customSpawners, boolean tickTime, CallbackInfo ci) {
		this.initCapabilities();
	}

	@Override
	public void initCapabilities() {
		this.gatherCapabilities();
		capabilityData = this.getDataStorage().computeIfAbsent(e -> LevelCapabilityData.load(e, this.getCapabilities()), () -> new LevelCapabilityData(this.getCapabilities()), LevelCapabilityData.ID);
		capabilityData.setCapabilities(this.getCapabilities());
	}
}
