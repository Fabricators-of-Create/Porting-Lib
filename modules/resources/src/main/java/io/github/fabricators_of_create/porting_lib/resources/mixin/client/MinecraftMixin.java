package io.github.fabricators_of_create.porting_lib.resources.mixin.client;

import io.github.fabricators_of_create.porting_lib.resources.events.AddPackFindersEvent;
import net.minecraft.client.Minecraft;

import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.PackType;

import net.minecraft.server.packs.repository.PackRepository;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	private PackRepository resourcePackRepository;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
	private void addClientResources(GameConfig gameConfig, CallbackInfo ci) {
		new AddPackFindersEvent(PackType.CLIENT_RESOURCES, this.resourcePackRepository::port_lib$addPackFinder, false).sendEvent();
	}
}
