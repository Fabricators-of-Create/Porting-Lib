package io.github.fabricators_of_create.porting_lib.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import com.google.common.collect.ImmutableMap;
import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;
import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;

public class PerspectiveMapWrapper implements BakedModel, TransformTypeDependentItemBakedModel {
	private final BakedModel parent;
	private final ImmutableMap<ModelTransformation.Mode, AffineTransformation> transforms;
	private final OverrideListWrapper overrides = new OverrideListWrapper();

	public PerspectiveMapWrapper(BakedModel parent, ImmutableMap<ModelTransformation.Mode, AffineTransformation> transforms) {
		this.parent = parent;
		this.transforms = transforms;
	}

	public PerspectiveMapWrapper(BakedModel parent, ModelBakeSettings state) {
		this(parent, getTransforms(state));
	}

	public static ImmutableMap<ModelTransformation.Mode, AffineTransformation> getTransforms(ModelBakeSettings state) {
		EnumMap<ModelTransformation.Mode, AffineTransformation> map = new EnumMap<>(ModelTransformation.Mode.class);
		for (ModelTransformation.Mode type : ModelTransformation.Mode.values()) {
			AffineTransformation tr = state.getPartTransformation(type);
			if (!tr.isIdentity()) {
				map.put(type, tr);
			}
		}
		return ImmutableMap.copyOf(map);
	}

	@SuppressWarnings("deprecation")
	public static ImmutableMap<ModelTransformation.Mode, AffineTransformation> getTransformsWithFallback(ModelBakeSettings state, ModelTransformation transforms) {
		EnumMap<ModelTransformation.Mode, AffineTransformation> map = new EnumMap<>(ModelTransformation.Mode.class);
		for (ModelTransformation.Mode type : ModelTransformation.Mode.values()) {
			AffineTransformation tr = state.getPartTransformation(type);
			if (!tr.isIdentity()) {
				map.put(type, tr);
			} else if (transforms.isTransformationDefined(type)) {
				map.put(type, TransformationHelper.toTransformation(transforms.getTransformation(type)));
			}
		}
		return ImmutableMap.copyOf(map);
	}

	@SuppressWarnings("deprecation")
	public static ImmutableMap<ModelTransformation.Mode, AffineTransformation> getTransforms(ModelTransformation transforms) {
		EnumMap<ModelTransformation.Mode, AffineTransformation> map = new EnumMap<>(ModelTransformation.Mode.class);
		for (ModelTransformation.Mode type : ModelTransformation.Mode.values()) {
			if (transforms.isTransformationDefined(type)) {
				map.put(type, TransformationHelper.toTransformation(transforms.getTransformation(type)));
			}
		}
		return ImmutableMap.copyOf(map);
	}

	public static BakedModel handlePerspective(BakedModel model, ImmutableMap<ModelTransformation.Mode, AffineTransformation> transforms, ModelTransformation.Mode cameraTransformType, MatrixStack mat) {
		AffineTransformation tr = transforms.getOrDefault(cameraTransformType, AffineTransformation.identity());
		if (!tr.isIdentity()) {
			tr.push(mat);
		}
		return model;
	}

	public static BakedModel handlePerspective(BakedModel model, ModelBakeSettings state, ModelTransformation.Mode cameraTransformType, MatrixStack mat) {
		AffineTransformation tr = state.getPartTransformation(cameraTransformType);
		if (!tr.isIdentity()) {
			tr.push(mat);
		}
		return model;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return parent.useAmbientOcclusion();
	}

	//  @Override public boolean useAmbientOcclusion(BlockState state) { return parent.useAmbientOcclusion(state); }
	@Override
	public boolean hasDepth() {
		return parent.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return parent.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return parent.isBuiltin();
	}

	@Override
	public Sprite getParticleSprite() {
		return parent.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return parent.getTransformation();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return parent.getQuads(state, side, rand);
	}

	@Override
	public ModelOverrideList getOverrides() {
		return overrides;
	}

	@Override
	public BakedModel handlePerspective(ModelTransformation.Mode cameraTransformType, MatrixStack poseStack) {
		return handlePerspective(this, transforms, cameraTransformType, poseStack);
	}

	private class OverrideListWrapper extends ModelOverrideList {
		public OverrideListWrapper() {
			super();
		}

		@Nullable
		@Override
		public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn, int seed) {
			model = parent.getOverrides().apply(parent, stack, worldIn, entityIn, seed);
			return new PerspectiveMapWrapper(model, transforms);
		}

//    @Override
//    public ImmutableList<BakedOverride> getOverrides()
//    {
//      return parent.getOverrides().getOverrides();
//    }
	}
}
