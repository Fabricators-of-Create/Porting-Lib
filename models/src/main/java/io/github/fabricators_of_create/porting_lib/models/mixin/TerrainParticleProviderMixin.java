package io.github.fabricators_of_create.porting_lib.models.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.model.CustomParticleIconModel;
import io.github.fabricators_of_create.porting_lib.models.extensions.BlockParticleOptionExtensions;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.BlockParticleOption;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TerrainParticle.Provider.class)
public class TerrainParticleProviderMixin {
	@Inject(method = "createParticle(Lnet/minecraft/core/particles/BlockParticleOption;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
	private void setCustomParticleIcon(BlockParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
		BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(type.getState());
		if (model instanceof CustomParticleIconModel particleIconModel)
			((TextureSheetParticleAccessor)cir.getReturnValue()).callSetSprite(particleIconModel.getParticleIcon(((RenderAttachedBlockView) level).getBlockEntityRenderAttachment(((BlockParticleOptionExtensions)type).getPos())));
	}
}
