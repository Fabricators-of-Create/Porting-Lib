package io.github.fabricators_of_create.porting_lib.model;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.SimpleBakedModel$BuilderAccessor;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.SimpleBakedModel.BuilderAccessor;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>> {
	static IModelBuilder<?> of(IModelConfiguration owner, ModelOverrideList overrides, Sprite particle) {
		return new Simple(BuilderAccessor.port_lib$create(owner.useSmoothLighting(), owner.isSideLit(), owner.isShadedInGui(), owner.getCameraTransforms(), overrides).setParticle(particle));
	}

	T addFaceQuad(Direction facing, BakedQuad quad);

	T addGeneralQuad(BakedQuad quad);

	BakedModel build();

	class Simple implements IModelBuilder<Simple> {
		final BasicBakedModel.Builder builder;

		Simple(BasicBakedModel.Builder builder) {
			this.builder = builder;
		}

		@Override
		public Simple addFaceQuad(Direction facing, BakedQuad quad) {
			builder.addQuad(facing, quad);
			return this;
		}

		@Override
		public Simple addGeneralQuad(BakedQuad quad) {
			builder.addQuad(quad);
			return this;
		}

		@Override
		public BakedModel build() {
			return builder.build();
		}
	}
}
