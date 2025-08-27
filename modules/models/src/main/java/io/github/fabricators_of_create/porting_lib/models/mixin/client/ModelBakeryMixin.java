package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.events.client.ModelEvent;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
	@Shadow
	abstract UnbakedModel getModel(ResourceLocation modelLocation);

	@Shadow
	protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void registerAdditionalModels(BlockColors blockColors, ProfilerFiller profilerFiller, Map modelResources, Map blockStateResources, CallbackInfo ci) {
		Set<ModelResourceLocation> additionalModels = new HashSet<>();
		(new ModelEvent.RegisterAdditional(additionalModels)).sendEvent();

		for (ModelResourceLocation model : additionalModels) {
			UnbakedModel unbakedModel = this.getModel(model.id());
			this.registerModelAndLoadDependencies(model, unbakedModel);
		}
	}
}
