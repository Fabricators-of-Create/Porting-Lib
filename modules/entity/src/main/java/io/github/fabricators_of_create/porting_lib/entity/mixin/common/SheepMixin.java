package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.extensions.VanillaIShearable;
import net.minecraft.world.entity.animal.Sheep;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sheep.class)
public class SheepMixin implements VanillaIShearable {
}
