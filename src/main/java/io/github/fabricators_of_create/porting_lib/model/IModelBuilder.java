package io.github.fabricators_of_create.porting_lib.model;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.SimpleBakedModel$BuilderAccessor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>> {
	static IModelBuilder<?> of(IGeometryBakingContext owner, ItemOverrides overrides, TextureAtlasSprite particle) {
		return new Simple(SimpleBakedModel$BuilderAccessor.port_lib$create(owner.useSmoothLighting(), owner.isSideLit(), owner.isShadedInGui(), owner.getCameraTransforms(), overrides).particle(particle));
	}

	T addFaceQuad(Direction facing, BakedQuad quad);

	T addGeneralQuad(BakedQuad quad);

	BakedModel build();

	class Simple implements IModelBuilder<Simple> {
		final SimpleBakedModel.Builder builder;

		Simple(SimpleBakedModel.Builder builder) {
			this.builder = builder;
		}

		@Override
		public Simple addFaceQuad(Direction facing, BakedQuad quad) {
			builder.addCulledFace(facing, quad);
			return this;
		}

		@Override
		public Simple addGeneralQuad(BakedQuad quad) {
			builder.addUnculledFace(quad);
			return this;
		}

		@Override
		public BakedModel build() {
			return builder.build();
		}
	}
}
