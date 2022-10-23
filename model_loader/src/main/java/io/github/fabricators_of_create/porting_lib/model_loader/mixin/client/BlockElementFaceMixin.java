package io.github.fabricators_of_create.porting_lib.model_loader.mixin.client;

import io.github.fabricators_of_create.porting_lib.model_loader.extensions.BlockElementFaceExtensions;
import net.minecraft.client.renderer.block.model.BlockElementFace;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockElementFace.class)
public class BlockElementFaceMixin implements BlockElementFaceExtensions {
	public int emissivity = 0;

	@Override
	public void setEmissivity(int emissivity) {
		this.emissivity = emissivity;
	}

	@Override
	public int getEmissivity() {
		return this.emissivity;
	}
}
