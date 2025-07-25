package io.github.fabricators_of_create.porting_lib.resources.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.resources.events.AddPackFindersEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.world.level.WorldDataConfiguration;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
	@Shadow
	@Nullable
	private PackRepository tempDataPackRepository;

	@Inject(method = "openFresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/WorldDataConfiguration;)Lnet/minecraft/server/WorldLoader$InitConfig;"))
	private static void addPacks(Minecraft minecraft, Screen screen, CallbackInfo ci, @Local(index = 2) PackRepository repository) {
		new AddPackFindersEvent(PackType.SERVER_DATA, repository::port_lib$addPackFinder, false).sendEvent();
	}

	@Inject(method = "getDataPackSelectionSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
	private void addPacks2(WorldDataConfiguration worldDataConfiguration, CallbackInfoReturnable<Pair<Path, PackRepository>> cir) {
		new AddPackFindersEvent(PackType.SERVER_DATA, this.tempDataPackRepository::port_lib$addPackFinder, false).sendEvent();
	}
}
