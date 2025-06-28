package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.extensions.client.PoseStackExtension;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(PoseStack.class)
public class PoseStackMixin implements PoseStackExtension {
}
