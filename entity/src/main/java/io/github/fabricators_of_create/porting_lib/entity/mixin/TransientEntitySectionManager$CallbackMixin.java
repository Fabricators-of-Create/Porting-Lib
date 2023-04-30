package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityChangeChunkSectionCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.TransientEntitySectionManager;

@Mixin(TransientEntitySectionManager.Callback.class)
public abstract class TransientEntitySectionManager$CallbackMixin<T extends EntityAccess> {
	@Shadow
	private long currentSectionKey;
	@Shadow
	@Final
	private T entity;

	@Shadow
	private EntitySection<T> currentSection;

	@Inject(
			method = "onMove",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/entity/EntitySection;add(Lnet/minecraft/world/level/entity/EntityAccess;)V"
			)
	)
	private void grabOldSectionKey(CallbackInfo ci,
								   @Share("oldSection") LocalRef<EntitySection<T>> oldSection,
								   @Share("oldSectionKey") LocalLongRef oldSectionKey) {
		oldSection.set(currentSection);
		oldSectionKey.set(currentSectionKey);
	}

	@Inject(method = "onMove", at = @At("TAIL"))
	private void fireSectionChangeEvent(CallbackInfo ci,
										@Share("oldSection") LocalRef<EntitySection<T>> oldSection,
										@Share("oldSectionKey") LocalLongRef oldSectionKey) {
		EntitySection<T> old = oldSection.get();
		// if old != null, both != null
		if (old != null && entity instanceof Entity e) {
			EntityChangeChunkSectionCallback.EVENT.invoker().onChunkSectionChange(
					e, old, oldSectionKey.get(), currentSection, currentSectionKey
			);
		}
	}
}
