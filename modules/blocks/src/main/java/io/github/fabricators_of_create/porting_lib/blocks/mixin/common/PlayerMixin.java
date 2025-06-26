package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.injects.PlayerInjection;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements PlayerInjection {
}
