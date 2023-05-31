package io.github.fabricators_of_create.porting_lib.models.mixin;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.model.CustomParticleIconModel;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
	@Nullable
	private static org.apache.commons.lang3.tuple.Pair<BlockState, BlockPos> getOverlayBlock(Player pPlayer) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for(int i = 0; i < 8; ++i) {
			double d0 = pPlayer.getX() + (double)(((float)((i >> 0) % 2) - 0.5F) * pPlayer.getBbWidth() * 0.8F);
			double d1 = pPlayer.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
			double d2 = pPlayer.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * pPlayer.getBbWidth() * 0.8F);
			blockpos$mutableblockpos.set(d0, d1, d2);
			BlockState blockstate = pPlayer.level.getBlockState(blockpos$mutableblockpos);
			if (blockstate.getRenderShape() != RenderShape.INVISIBLE && blockstate.isViewBlocking(pPlayer.level, blockpos$mutableblockpos)) {
				return org.apache.commons.lang3.tuple.Pair.of(blockstate, blockpos$mutableblockpos.immutable());
			}
		}

		return null;
	}

	@ModifyExpressionValue(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getParticleIcon(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
	private static TextureAtlasSprite isParticleModel(TextureAtlasSprite sprite, Minecraft minecraft, PoseStack poseStack) {
		Player player = minecraft.player;
		Pair<BlockState, BlockPos> pair = getOverlayBlock(player);
		BakedModel model = minecraft.getBlockRenderer().getBlockModelShaper().getBlockModel(pair.getLeft());
		if (model instanceof CustomParticleIconModel particleIconModel)
			return particleIconModel.getParticleIcon(((RenderAttachedBlockView)minecraft.level).getBlockEntityRenderAttachment(pair.getRight()));
		return sprite;
	}
}
