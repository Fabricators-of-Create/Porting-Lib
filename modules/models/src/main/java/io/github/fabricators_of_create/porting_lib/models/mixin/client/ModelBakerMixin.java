package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.injections.ModelBakerInjection;
import net.minecraft.client.resources.model.ModelBaker;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelBaker.class)
public interface ModelBakerMixin extends ModelBakerInjection {
}
