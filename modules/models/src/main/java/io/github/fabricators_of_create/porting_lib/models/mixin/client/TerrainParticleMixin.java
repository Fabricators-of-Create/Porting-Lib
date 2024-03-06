package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import io.github.fabricators_of_create.porting_lib.models.CustomParticleIconModel;
import io.github.fabricators_of_create.porting_lib.models.extensions.TerrainParticleExtensions;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(TerrainParticle.class)
public abstract class TerrainParticleMixin extends TextureSheetParticle implements TerrainParticleExtensions {
	protected TerrainParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
		super(clientLevel, d, e, f);
	}

	@Override
	public TerrainParticle updateSprite(BlockState state, BlockPos pos) {
		Minecraft mc = Minecraft.getInstance();
		BakedModel model = mc.getModelManager().getBlockModelShaper().getBlockModel(state);
		if (model instanceof CustomParticleIconModel custom && mc.level != null) {
			Object data = mc.level.getBlockEntityRenderData(pos);
			this.setSprite(custom.getParticleIcon(data));
		}
		return (TerrainParticle) (Object) this;
	}
}
