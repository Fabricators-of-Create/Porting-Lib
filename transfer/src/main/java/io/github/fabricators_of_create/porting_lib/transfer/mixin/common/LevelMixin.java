package io.github.fabricators_of_create.porting_lib.transfer.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.transfer.LevelExtensions;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class LevelMixin implements LevelExtensions {
}
