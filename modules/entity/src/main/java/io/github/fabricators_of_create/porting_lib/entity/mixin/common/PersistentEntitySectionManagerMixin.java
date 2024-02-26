package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin<T extends EntityAccess> {
	@Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
	public void port_lib$addEntityEvent(T entityAccess, boolean loadedFromDisk, CallbackInfoReturnable<Boolean> cir) {
		if (entityAccess instanceof Entity entity && EntityEvents.ON_JOIN_WORLD.invoker().onJoinWorld(entity, entity.level(), loadedFromDisk))
			cir.setReturnValue(false);
	}
}
