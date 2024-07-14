package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityJoinLevelEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.tick.EntityTickEvent;
import io.github.fabricators_of_create.porting_lib.entity.ext.LevelExt;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements LevelExt {

	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}

	@ModifyReturnValue(method = "getEntityOrPart", at = @At("RETURN"))
	public Entity port_lib$getMultipart(Entity entity, int id) {
		if (entity == null) {
			Int2ObjectMap<PartEntity<?>> partEntityMap = getPartEntityMap();
			if (partEntityMap != null) {
				return partEntityMap.get(id);
			}
		}
		return entity;
	}

	@Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
	public void port_lib$addEntityEvent(ServerPlayer serverPlayer, CallbackInfo ci) {
		EntityJoinLevelEvent event = new EntityJoinLevelEvent(serverPlayer, this);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}

	@WrapOperation(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private void preEntityTick(Entity instance, Operation<Void> original) {
		if (!EntityHooks.fireEntityTickPre(instance).isCanceled()) {
			original.call(instance);
			EntityHooks.fireEntityTickPost(instance);
		}
	}
}
