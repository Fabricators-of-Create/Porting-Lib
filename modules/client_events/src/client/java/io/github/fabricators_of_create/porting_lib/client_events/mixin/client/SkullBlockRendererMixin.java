package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;

import net.minecraft.world.level.block.SkullBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin {
	@ModifyReturnValue(method = "createModel", at = @At(value = "RETURN", ordinal = 1))
	private static SkullModelBase createModdedSkullModel(SkullModelBase original, EntityModelSet modelSet, SkullBlock.Type type) {
		if (original == null)
			return ClientEventHooks.getModdedSkullModel(modelSet, type);
		return original;
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRendererProvider$Context;playerSkinRenderCache()Lnet/minecraft/client/renderer/PlayerSkinRenderCache;"))
	private void registerSkinModels(BlockEntityRendererProvider.Context context, CallbackInfo ci) {
		ClientEventHooks.reloadSkullModels();
	}
}

