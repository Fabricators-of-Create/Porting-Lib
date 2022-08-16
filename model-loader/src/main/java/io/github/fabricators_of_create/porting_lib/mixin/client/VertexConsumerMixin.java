package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.extensions.VertexConsumerExtension;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin extends VertexConsumerExtension {
}
