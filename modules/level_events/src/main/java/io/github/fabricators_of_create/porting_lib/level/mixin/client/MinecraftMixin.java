package io.github.fabricators_of_create.porting_lib.level.mixin.client;

import io.github.fabricators_of_create.porting_lib.level.events.LevelEvent;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public ClientLevel level;

	@Inject(method = "setLevel", at = @At("HEAD"))
	private void onUnload(ClientLevel clientLevel, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
		if (this.level != null) new LevelEvent.Unload(this.level).sendEvent();
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
			ordinal = 0,
			shift = At.Shift.AFTER
	))
	private void onDisconnect(Screen screen, boolean bl, CallbackInfo ci) {
		new LevelEvent.Unload(this.level).sendEvent();
	}
}
