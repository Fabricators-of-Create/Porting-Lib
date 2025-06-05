package io.github.fabricators_of_create.porting_lib.models;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.block.model.BakedQuad;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 * @see FabricQuadTransformers
 */
public interface IQuadTransformer {
	int STRIDE = QuadView.VANILLA_VERTEX_STRIDE;
	int POSITION = findOffset(VertexFormatElement.POSITION);
	int COLOR = findOffset(VertexFormatElement.COLOR);
	int UV0 = findOffset(VertexFormatElement.UV0);
	int UV1 = findOffset(VertexFormatElement.UV1);
	int UV2 = findOffset(VertexFormatElement.UV2);
	int NORMAL = findOffset(VertexFormatElement.NORMAL);

	void processInPlace(BakedQuad quad);

	default void processInPlace(List<BakedQuad> quads) {
		for (BakedQuad quad : quads)
			processInPlace(quad);
	}

	default BakedQuad process(BakedQuad quad) {
		var copy = copy(quad);
		processInPlace(copy);
		return copy;
	}

	default List<BakedQuad> process(List<BakedQuad> inputs) {
		return inputs.stream().map(IQuadTransformer::copy).peek(this::processInPlace).toList();
	}

	default IQuadTransformer andThen(IQuadTransformer other) {
		return quad -> {
			processInPlace(quad);
			other.processInPlace(quad);
		};
	}

	private static BakedQuad copy(BakedQuad quad) {
		var vertices = quad.getVertices();
		return new BakedQuad(Arrays.copyOf(vertices, vertices.length), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade()/*, quad.hasAmbientOcclusion()*/);
	}

	private static int findOffset(VertexFormatElement element) {
		if (DefaultVertexFormat.BLOCK.contains(element)) {
			// Divide by 4 because we want the int offset
			return DefaultVertexFormat.BLOCK.getOffset(element) / 4;
		}
		return -1;
	}
}
