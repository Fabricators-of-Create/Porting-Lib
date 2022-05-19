package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.TransientEntitySectionManager;

@Mixin(TransientEntitySectionManager.Callback.class)
public class TransientEntitySectionManager$CallbackMixin {
	@Shadow
	private long currentSectionKey;

	@Unique
	private Entity port_lib$realEntity;
	@Unique
	private long port_lib$oldSectionKey;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$init(TransientEntitySectionManager transientEntitySectionManager, EntityAccess entityAccess, long l, EntitySection entitySection, CallbackInfo ci) {
		this.port_lib$realEntity = entityAccess instanceof Entity e ? e : null;
	}

	@Inject(method = "onMove", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$onEntityEnter(CallbackInfo ci, BlockPos blockPos, long i) {
		if (this.port_lib$realEntity != null) EntityEvents.ENTERING_SECTION.invoker().onEntityEnterSection(this.port_lib$realEntity, port_lib$oldSectionKey, i);
	}

	@Inject(method = "onMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/entity/EntitySection;add(Lnet/minecraft/world/level/entity/EntityAccess;)V", shift = At.Shift.AFTER))
	public void port_lib$updateOldKey(CallbackInfo ci) {
		port_lib$oldSectionKey = currentSectionKey;
	}
}
