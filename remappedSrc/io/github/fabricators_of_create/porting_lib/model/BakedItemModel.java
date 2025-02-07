package io.github.fabricators_of_create.porting_lib.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BakedItemModel implements BakedModel, TransformTypeDependentItemBakedModel {
	protected final ImmutableList<BakedQuad> quads;
	protected final Sprite particle;
	protected final ImmutableMap<Mode, AffineTransformation> transforms;
	protected final ModelOverrideList overrides;
	protected final BakedModel guiModel;
	protected final boolean isSideLit;

	public BakedItemModel(ImmutableList<BakedQuad> quads, Sprite particle, ImmutableMap<Mode, AffineTransformation> transforms, ModelOverrideList overrides, boolean untransformed, boolean isSideLit)
	{
		this.quads = quads;
		this.particle = particle;
		this.transforms = transforms;
		this.overrides = overrides;
		this.isSideLit = isSideLit;
		this.guiModel = untransformed && hasGuiIdentity(transforms) ? new BakedGuiItemModel<>(this) : null;
	}

	private static boolean hasGuiIdentity(ImmutableMap<Mode, AffineTransformation> transforms)
	{
		AffineTransformation guiTransform = transforms.get(Mode.GUI);
		return guiTransform == null || guiTransform.isIdentity();
	}

	@Override public boolean useAmbientOcclusion() { return true; }
	@Override public boolean hasDepth() { return false; }
	@Override public boolean isSideLit() { return isSideLit; }
	@Override public boolean isBuiltin() { return false; }
	@Override public Sprite getParticleSprite() { return particle; }

	@Override
	public ModelTransformation getTransformation() {
		return ModelTransformation.NONE;
	}

	@Override public ModelOverrideList getOverrides() { return overrides; }

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
	{
		if (side == null)
		{
			return quads;
		}
		return ImmutableList.of();
	}

	@Override
	public BakedModel handlePerspective(Mode type, MatrixStack poseStack) {
		if (type == Mode.GUI && this.guiModel != null)
		{
			return ((TransformTypeDependentItemBakedModel)this.guiModel).handlePerspective(type, poseStack);
		}
		return PerspectiveMapWrapper.handlePerspective(this, transforms, type, poseStack);
	}

	public static class BakedGuiItemModel<T extends BakedItemModel> extends ForwardingBakedModel implements TransformTypeDependentItemBakedModel {
		private final ImmutableList<BakedQuad> quads;

		public BakedGuiItemModel(T originalModel) {
			wrapped = originalModel;
			ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
			for (BakedQuad quad : originalModel.quads) {
				if (quad.getFace() == Direction.SOUTH) {
					builder.add(quad);
				}
			}
			this.quads = builder.build();
		}

		@Override
		public List<BakedQuad> getQuads (@Nullable BlockState state, @Nullable Direction side, Random rand) {
			if(side == null) {
				return quads;
			}
			return ImmutableList.of();
		}

		@Override
		public BakedModel handlePerspective(Mode type, MatrixStack poseStack) {
			if (type == Mode.GUI) {
				return PerspectiveMapWrapper.handlePerspective(this, ((BakedItemModel)wrapped).transforms, type, poseStack);
			}
			return ((TransformTypeDependentItemBakedModel)this.wrapped).handlePerspective(type, poseStack);
		}
	}
}

