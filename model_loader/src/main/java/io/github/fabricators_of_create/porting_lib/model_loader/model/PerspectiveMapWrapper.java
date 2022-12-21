package io.github.fabricators_of_create.porting_lib.model_loader.model;

import java.util.EnumMap;
import java.util.List;

import net.minecraftforge.client.model.generators.TransformationHelper;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.model_loader.render.TransformTypeDependentItemBakedModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class PerspectiveMapWrapper implements BakedModel, TransformTypeDependentItemBakedModel {
	private final BakedModel parent;
	private final ImmutableMap<ItemTransforms.TransformType, Transformation> transforms;
	private final OverrideListWrapper overrides = new OverrideListWrapper();

	public PerspectiveMapWrapper(BakedModel parent, ImmutableMap<ItemTransforms.TransformType, Transformation> transforms) {
		this.parent = parent;
		this.transforms = transforms;
	}

	public PerspectiveMapWrapper(BakedModel parent, ModelState state) {
		this(parent, getTransforms(state));
	}

	public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransforms(ModelState state) {
		EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
		for (ItemTransforms.TransformType type : ItemTransforms.TransformType.values()) {
			Transformation tr = state.getPartTransformation(type);
			if (!tr.isIdentity()) {
				map.put(type, tr);
			}
		}
		return ImmutableMap.copyOf(map);
	}

	@SuppressWarnings("deprecation")
	public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransformsWithFallback(ModelState state, ItemTransforms transforms) {
		EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
		for (ItemTransforms.TransformType type : ItemTransforms.TransformType.values()) {
			Transformation tr = state.getPartTransformation(type);
			if (!tr.isIdentity()) {
				map.put(type, tr);
			} else if (transforms.hasTransform(type)) {
				map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
			}
		}
		return ImmutableMap.copyOf(map);
	}

	@SuppressWarnings("deprecation")
	public static ImmutableMap<ItemTransforms.TransformType, Transformation> getTransforms(ItemTransforms transforms) {
		EnumMap<ItemTransforms.TransformType, Transformation> map = new EnumMap<>(ItemTransforms.TransformType.class);
		for (ItemTransforms.TransformType type : ItemTransforms.TransformType.values()) {
			if (transforms.hasTransform(type)) {
				map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
			}
		}
		return ImmutableMap.copyOf(map);
	}

	public static BakedModel handlePerspective(BakedModel model, ImmutableMap<ItemTransforms.TransformType, Transformation> transforms, ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
		Transformation tr = transforms.getOrDefault(cameraTransformType, Transformation.identity());
		if (!tr.isIdentity()) {
			tr.push(mat);
		}
		return model;
	}

	public static BakedModel handlePerspective(BakedModel model, ModelState state, ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
		Transformation tr = state.getPartTransformation(cameraTransformType);
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
	public boolean isGui3d() {
		return parent.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return parent.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return parent.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return parent.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return parent.getTransforms();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return parent.getQuads(state, side, rand);
	}

	@Override
	public ItemOverrides getOverrides() {
		return overrides;
	}

	@Override
	public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack) {
		return handlePerspective(this, transforms, cameraTransformType, poseStack);
	}

	private class OverrideListWrapper extends ItemOverrides {
		public OverrideListWrapper() {
			super();
		}

		@Nullable
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int seed) {
			model = parent.getOverrides().resolve(parent, stack, worldIn, entityIn, seed);
			return new PerspectiveMapWrapper(model, transforms);
		}

//    @Override
//    public ImmutableList<BakedOverride> getOverrides()
//    {
//      return parent.getOverrides().getOverrides();
//    }
	}
}
