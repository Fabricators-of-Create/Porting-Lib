package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.world.entity.monster.MagmaCube;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaCube.class)
public class MagmaCubeMixin {
	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void onJump(CallbackInfo ci) {
		EntityHooks.onLivingJump(MixinHelper.cast(this));
	}
}
