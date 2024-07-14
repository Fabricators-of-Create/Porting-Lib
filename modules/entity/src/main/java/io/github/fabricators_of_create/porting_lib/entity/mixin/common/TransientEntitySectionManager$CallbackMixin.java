package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/level/entity/TransientEntitySectionManager$Callback")
public abstract class TransientEntitySectionManager$CallbackMixin<T extends EntityAccess>  {
	@Shadow
	private long currentSectionKey;
	@Shadow
	@Final
	private T entity;

	@Inject(
			method = "onMove",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/entity/EntitySection;add(Lnet/minecraft/world/level/entity/EntityAccess;)V",
					shift = At.Shift.AFTER
			)
	)
	public void grabOldKey(CallbackInfo ci, @Share("oldKey") LocalLongRef oldKey) {
		oldKey.set(this.currentSectionKey);
	}

	@Inject(
			method = "onMove",
			at = @At("TAIL")
	)
	public void afterSectionChange(CallbackInfo ci, @Local(ordinal = 0) BlockPos pos,
								   @Local(ordinal = 0) long newKey, @Share("oldKey") LocalLongRef oldKey) {
		if (newKey != this.currentSectionKey) {
			if (this.entity instanceof Entity realEntity) {
				EntityHooks.onEntityEnterSection(realEntity, oldKey.get(), newKey);
			}
		}
	}
}
