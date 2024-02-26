package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.PoseStackExtensions;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(PoseStack.class)
public class PoseStackMixin implements PoseStackExtensions {
}
