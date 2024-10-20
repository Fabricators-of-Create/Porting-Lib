package io.github.fabricators_of_create.porting_lib.models;

import java.util.List;

import io.github.fabricators_of_create.porting_lib.models.geometry.EmptyModel;
import io.github.fabricators_of_create.porting_lib.textures.UnitTextureAtlasSprite;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

/**
 * Base interface for any object that collects culled and unculled faces and bakes them into a model.
 * <p>
 * Provides a generic base implementation via {@link #of(boolean, boolean, boolean, ItemTransforms, ItemOverrides, TextureAtlasSprite, RenderTypeGroup)}
 * and a quad-collecting alternative via {@link #collecting(List)}.
 */
public interface IModelBuilder<T extends IModelBuilder<T>> {
	/**
	 * Creates a new model builder that uses the provided attributes in the final baked model.
	 */
	static IModelBuilder<?> of(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
							   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
							   RenderTypeGroup renderTypes) {
		return new Simple(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes);
	}

	/**
	 * Creates a new model builder that collects quads to the provided list, returning
	 * {@linkplain EmptyModel#BAKED an empty model} if you call {@link #build()}.
	 */
	static IModelBuilder<?> collecting(List<BakedQuad> quads) {
		return new Collecting(quads);
	}

	T addCulledFace(Direction facing, BakedQuad quad);

	T addUnculledFace(BakedQuad quad);

	T addFace(QuadView quad);

	BakedModel build();

	class Simple implements IModelBuilder<Simple> {
		private final MeshBuilder builder;
		private final boolean hasAmbientOcclusion, usesBlockLight, isGui3d;
		private final ItemTransforms transforms;
		private final ItemOverrides overrides;
		private final TextureAtlasSprite particle;
		private final RenderTypeGroup renderTypes;
		private final RenderMaterial material;

		private Simple(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
					   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
					   RenderTypeGroup renderTypes) {
			this.builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();//new SimpleBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides).particle(particle);
			this.hasAmbientOcclusion = hasAmbientOcclusion;
			this.usesBlockLight = usesBlockLight;
			this.isGui3d = isGui3d;
			this.transforms = transforms;
			this.overrides = overrides;
			this.particle = particle;
			this.renderTypes = renderTypes;
			this.material = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(BlendMode.fromRenderLayer(renderTypes.block())).find();
		}

		@Override
		public Simple addCulledFace(Direction facing, BakedQuad quad) {
			builder.getEmitter().fromVanilla(quad, material, facing).emit();
			return this;
		}

		@Override
		public Simple addUnculledFace(BakedQuad quad) {
			builder.getEmitter().fromVanilla(quad, material, null).emit();
			return this;
		}

		@Override
		public Simple addFace(QuadView quad) {
			builder.getEmitter().copyFrom(quad).emit();
			return this;
		}

		@Deprecated
		@Override
		public BakedModel build() {
			return new MeshBakedModel(builder.build(), hasAmbientOcclusion, usesBlockLight, isGui3d, particle, transforms, overrides);
		}
	}

	class Collecting implements IModelBuilder<Collecting> {
		private final List<BakedQuad> quads;

		private Collecting(List<BakedQuad> quads) {
			this.quads = quads;
		}

		@Override
		public Collecting addCulledFace(Direction facing, BakedQuad quad) {
			quads.add(quad);
			return this;
		}

		@Override
		public Collecting addUnculledFace(BakedQuad quad) {
			quads.add(quad);
			return this;
		}

		@Override
		public Collecting addFace(QuadView quad) {
			quads.add(quad.toBakedQuad(UnitTextureAtlasSprite.INSTANCE));
			return this;
		}

		@Override
		public BakedModel build() {
			return EmptyModel.BAKED;
		}
	}
}
