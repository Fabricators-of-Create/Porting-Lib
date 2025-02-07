package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.TerrainParticleExtensions;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.model.CustomParticleIconModel;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(BlockDustParticle.class)
public abstract class TerrainParticleMixin extends SpriteBillboardParticle implements TerrainParticleExtensions {
	protected TerrainParticleMixin(ClientWorld clientLevel, double d, double e, double f) {
		super(clientLevel, d, e, f);
	}

	@Override
	public BlockDustParticle updateSprite(BlockState state, BlockPos pos) {
		MinecraftClient mc = MinecraftClient.getInstance();
		BakedModel model = mc.getBakedModelManager().getBlockModels().getModel(state);
		Sprite sprite;
		if (model instanceof CustomParticleIconModel custom && mc.world instanceof RenderAttachedBlockView view) {
			Object data = view.getBlockEntityRenderAttachment(pos);
			sprite = custom.getParticleIcon(data);
		} else {
			sprite = model.getParticleSprite();
		}
		setSprite(sprite);
		return (BlockDustParticle) (Object) this;
	}
}
