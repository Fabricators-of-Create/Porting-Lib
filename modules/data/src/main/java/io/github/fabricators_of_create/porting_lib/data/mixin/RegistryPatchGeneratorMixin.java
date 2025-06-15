package io.github.fabricators_of_create.porting_lib.data.mixin;

import io.github.fabricators_of_create.porting_lib.data.PortingLibDataHack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.RegistryPatchGenerator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryPatchGenerator.class)
public class RegistryPatchGeneratorMixin {
	@Inject(method = "method_54839", at = @At(value = "FIELD", target = "Lnet/minecraft/resources/RegistryDataLoader;WORLDGEN_REGISTRIES:Ljava/util/List;"))
	private static void enableWithDimensions(RegistrySetBuilder registrySetBuilder, HolderLookup.Provider provider, CallbackInfoReturnable<RegistrySetBuilder.PatchedRegistries> cir) {
		PortingLibDataHack.inPatch = true;
	}
}
