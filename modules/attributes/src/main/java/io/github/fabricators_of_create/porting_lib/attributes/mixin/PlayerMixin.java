package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import io.github.fabricators_of_create.porting_lib.attributes.extensions.PlayerAttributesExtensions;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements PlayerAttributesExtensions {
	// no need to do anything, all methods are defaulted.
}
