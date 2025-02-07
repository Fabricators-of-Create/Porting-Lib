package io.github.fabricators_of_create.porting_lib.util;

import java.util.List;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.ItemRendererAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class ItemRendererHelper {

	public static void renderQuadList(ItemRenderer renderer, MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
		get(renderer).port_lib$renderQuadList(matrices, vertices, quads, stack, light, overlay);
	}

	private static ItemRendererAccessor get(ItemRenderer renderer) {
		return MixinHelper.cast(renderer);
	}

	private ItemRendererHelper() {}
}
