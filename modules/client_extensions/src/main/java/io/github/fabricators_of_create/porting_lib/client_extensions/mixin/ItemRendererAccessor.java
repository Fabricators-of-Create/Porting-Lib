package io.github.fabricators_of_create.porting_lib.client_extensions.mixin;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
	@Accessor
	BlockEntityWithoutLevelRenderer getBlockEntityRenderer();
}
