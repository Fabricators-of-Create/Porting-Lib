package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.AddPackFindersEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {
	@Inject(method = "createPackRepository(Ljava/nio/file/Path;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN"))
	private static void firePackFinders(Path path, CallbackInfoReturnable<PackRepository> cir) {
		new AddPackFindersEvent(PackType.SERVER_DATA, cir.getReturnValue()::pl$addPackFinder).sendEvent();
	}
}
