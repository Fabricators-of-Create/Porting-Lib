package io.github.fabricators_of_create.porting_lib.models;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class RenderTypeModel extends ForwardingBakedModel {
	protected final RenderMaterial material;
	public RenderTypeModel(BakedModel owner, RenderType renderType) {
		this.wrapped = owner;
		this.material = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.fromRenderLayer(renderType)).find();
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(quad -> {
			quad.material(this.material);
			return true;
		});
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(quad -> {
			quad.material(this.material);
			return true;
		});
		super.emitItemQuads(stack, randomSupplier, context);
		context.popTransform();
	}
}
