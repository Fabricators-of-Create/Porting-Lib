package io.github.fabricators_of_create.porting_lib.fake_players.mixin.common;

import io.github.fabricators_of_create.porting_lib.fake_players.extensions.ServerPlayerExtension;
import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerExtension {
}
