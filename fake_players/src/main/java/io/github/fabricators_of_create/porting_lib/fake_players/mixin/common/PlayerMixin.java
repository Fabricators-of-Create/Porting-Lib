package io.github.fabricators_of_create.porting_lib.fake_players.mixin.common;

import io.github.fabricators_of_create.porting_lib.fake_players.extensions.PlayerExtensions;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements PlayerExtensions {
}
