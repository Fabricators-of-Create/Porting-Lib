package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.resources.events.AddPackFindersEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPacksSource.class)
public abstract class ServerPacksSourceMixin {
	@ModifyReturnValue(method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN"))
	private static PackRepository firePackFinders(PackRepository original) {
		new AddPackFindersEvent(PackType.SERVER_DATA, original::port_lib$addPackFinder, false).sendEvent();
		return original;
	}

	@ModifyReturnValue(method = "createVanillaTrustedRepository", at = @At("RETURN"))
	private static PackRepository firePackFinders2(PackRepository original) {
		new AddPackFindersEvent(PackType.SERVER_DATA, original::port_lib$addPackFinder, true).sendEvent();
		return original;
	}
}
