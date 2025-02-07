package io.github.fabricators_of_create.porting_lib.model;

import io.github.fabricators_of_create.porting_lib.util.client.VertexUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * Implements a simple renderable consisting of a hierarchy of parts, where each part can contain a number of meshes.
 * Each mesh pairs a texture, with a set of quads.
 */
public class SimpleRenderable implements IRenderable<MultipartTransforms> {
	private final List<Part> parts = new ArrayList<>();

	private SimpleRenderable() {}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumerProvider bufferSource, Function<Identifier, RenderLayer> renderTypeFunction, int lightmapCoord, int overlayCoord, float partialTicks, MultipartTransforms renderValues) {
		for(var part : parts) {
			part.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
		}
	}

	private static class Part {
		private final String name;
		private final List<Part> parts = new ArrayList<>();
		private final List<Mesh> meshes = new ArrayList<>();

		public Part(String name)
		{
			this.name = name;
		}

		public void render(MatrixStack poseStack, VertexConsumerProvider bufferSource, Function<Identifier, RenderLayer> renderTypeFunction, int lightmapCoord, int overlayCoord, MultipartTransforms renderValues) {
			Matrix4f matrix = renderValues.getPartValues(name);
			if (matrix != null) {
				poseStack.push();
				poseStack.mulPoseMatrix(matrix);
			}

			for(var part : parts) {
				part.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
			}

			for(var mesh : meshes) {
				mesh.render(poseStack, bufferSource, renderTypeFunction, lightmapCoord, overlayCoord, renderValues);
			}

			if (matrix != null) {
				poseStack.pop();
			}
		}
	}

	private static class Mesh {
		private final Identifier texture;
		private final List<BakedQuad> quads = new ArrayList<>();

		public Mesh(Identifier texture)
		{
			this.texture = texture;
		}

		public void render(MatrixStack poseStack, VertexConsumerProvider bufferSource, Function<Identifier, RenderLayer> renderTypeFunction, int lightmapCoord, int overlayCoord, MultipartTransforms renderValues) {
			var consumer = bufferSource.getBuffer(renderTypeFunction.apply(texture));
			for(var quad : quads) {
				VertexUtils.putBulkData(consumer, poseStack.peek(), quad, 1, 1, 1, 1, lightmapCoord, overlayCoord, true);
			}
		}
	}

	public static class Builder {
		private final SimpleRenderable renderable = new SimpleRenderable();

		private Builder() {}

		public PartBuilder<Builder> child(String name) {
			var child = new Part(name);
			renderable.parts.add(child);
			return new PartBuilder<>(this, child);
		}

		public SimpleRenderable get()
		{
			return renderable;
		}
	}

	public static class PartBuilder<T> {
		private final T parent;
		private final Part part;

		private PartBuilder(T parent, Part part) {
			this.parent = parent;
			this.part = part;
		}

		public PartBuilder<PartBuilder<T>> child(String name) {
			var child = new Part(part.name + "/" + name);
			this.part.parts.add(child);
			return new PartBuilder<>(this, child);
		}

		public PartBuilder<T> addMesh(Identifier texture, List<BakedQuad> quads) {
			var mesh = new Mesh(texture);
			mesh.quads.addAll(quads);
			part.meshes.add(mesh);
			return this;
		}

		public T end() {
			return parent;
		}
	}
}
