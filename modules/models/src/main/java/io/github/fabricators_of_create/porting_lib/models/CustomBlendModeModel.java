package io.github.fabricators_of_create.porting_lib.models;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class CustomBlendModeModel extends ForwardingBakedModel {
	private final QuadTransform transform;


	public CustomBlendModeModel(BakedModel owner, BlendMode blendMode) {
		this.wrapped = owner;

		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		MaterialFinder finder = renderer == null ? null : renderer.materialFinder();
		this.transform = finder == null ? quad -> true : quad -> {
			RenderMaterial newMaterial = finder.copyFrom(quad.material()).blendMode(blendMode).find();
			quad.material(newMaterial);
			return true;
		};
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(this.transform);
		super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		context.popTransform();
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		context.pushTransform(this.transform);
		super.emitItemQuads(stack, randomSupplier, context);
		context.popTransform();
	}
}
