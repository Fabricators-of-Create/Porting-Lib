package io.github.fabricators_of_create.porting_lib.extensions.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface TransformationClientExtensions {
	@Environment(EnvType.CLIENT)
	default void push(PoseStack stack) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
