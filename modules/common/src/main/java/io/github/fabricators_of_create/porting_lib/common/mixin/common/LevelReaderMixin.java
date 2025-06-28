package io.github.fabricators_of_create.porting_lib.common.mixin.common;

import io.github.fabricators_of_create.porting_lib.common.injects.LevelReaderInjection;
import net.minecraft.world.level.LevelReader;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelReader.class)
public interface LevelReaderMixin extends LevelReaderInjection {
}
