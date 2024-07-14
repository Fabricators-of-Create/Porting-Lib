package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin {
	@Shadow
	@Final
	private File playerDir;

	@Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;safeReplaceFile(Ljava/nio/file/Path;Ljava/nio/file/Path;Ljava/nio/file/Path;)V", shift = At.Shift.AFTER))
	private void onPlayerSaving(Player player, CallbackInfo ci) {
		EntityHooks.firePlayerSavingEvent(player, this.playerDir, player.getStringUUID());
	}

	@Inject(method = "method_55788", at = @At("RETURN"))
	private void onPlayerLoading(Player player, CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		EntityHooks.firePlayerLoadingEvent(player, this.playerDir, player.getStringUUID());
	}
}
