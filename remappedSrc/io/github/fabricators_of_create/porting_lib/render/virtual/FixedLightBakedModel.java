package io.github.fabricators_of_create.porting_lib.render.virtual;

import java.util.Random;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class FixedLightBakedModel extends ForwardingBakedModel {
	private static final ThreadLocal<FixedLightBakedModel> THREAD_LOCAL = ThreadLocal.withInitial(FixedLightBakedModel::new);

	protected int light;

	protected FixedLightBakedModel() {
	}

	public static BakedModel wrap(BakedModel model, int light) {
		FixedLightBakedModel wrapper = THREAD_LOCAL.get();
		wrapper.wrapped = model;
		wrapper.light = light;
		return wrapper;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(quad -> {
			quad.lightmap(light, light, light, light);
			return true;
		});
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(quad -> {
			quad.lightmap(light, light, light, light);
			return true;
		});
		super.emitItemQuads(stack, randomSupplier, context);
		context.popTransform();
	}
}
