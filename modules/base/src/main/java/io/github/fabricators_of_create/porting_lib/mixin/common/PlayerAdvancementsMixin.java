package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.common.AdvancementEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
	@Shadow
	private ServerPlayer player;

	@Inject(method = "award", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onAdvancementProgress(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir, boolean bl, AdvancementProgress advancementProgress) {
		AdvancementEvent.AdvancementProgressEvent event = new AdvancementEvent.AdvancementProgressEvent(this.player, advancement, advancementProgress, criterionName, AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT);
		event.sendEvent();
	}

	@Inject(method = "revoke", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onAdvancementRevoke(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir, boolean bl, AdvancementProgress advancementProgress) {
		AdvancementEvent.AdvancementProgressEvent event = new AdvancementEvent.AdvancementProgressEvent(this.player, advancement, advancementProgress, criterionName, AdvancementEvent.AdvancementProgressEvent.ProgressType.REVOKE);
		event.sendEvent();
	}

	@Inject(method = "method_53637", at = @At("TAIL"))
	public void onAwardAdvancement(AdvancementHolder advancementHolder, DisplayInfo display, CallbackInfo ci) {
		AdvancementEvent.AdvancementEarnEvent event = new AdvancementEvent.AdvancementEarnEvent(this.player, advancementHolder);
		event.sendEvent();
	}
}
