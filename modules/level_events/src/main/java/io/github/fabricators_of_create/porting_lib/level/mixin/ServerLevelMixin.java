package io.github.fabricators_of_create.porting_lib.level.mixin;

import io.github.fabricators_of_create.porting_lib.level.events.SleepFinishedTimeEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
	private long sleepFinishedEvent(long newTime) {
		SleepFinishedTimeEvent event = new SleepFinishedTimeEvent((ServerLevel) (Object) this, newTime, getDayTime());
		event.sendEvent();
		return event.getNewTime();
	}
}
