package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.EntityCollisionContextExtensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

@Mixin(EntityCollisionContext.class)
public abstract class EntityCollisionContextMixin implements EntityCollisionContextExtensions {
	@Unique
	private Entity port_lib$cachedEntity;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
	private void port_lib$onTailEntityInit(Entity entity, CallbackInfo ci) {
		port_lib$cachedEntity = entity;
	}

	@Override
	public @Nullable Entity getCachedEntity() {
		return port_lib$cachedEntity;
	}
}
