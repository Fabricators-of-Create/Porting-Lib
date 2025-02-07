package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.ModelStateExtensions;
import net.minecraft.client.render.model.ModelBakeSettings;

@Mixin(ModelBakeSettings.class)
public interface ModelStateMixin extends ModelStateExtensions {
}
