package io.github.fabricators_of_create.porting_lib.model.obj;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ObjBakedModel implements BakedModel, FabricBakedModel {

	private final List<Mesh> meshes;

	public ObjBakedModel(List<Mesh> meshes) {
		this.meshes = meshes;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		meshes.forEach(mesh -> {
			mesh.quads.forEach(bakedQuad -> {
				context.pushTransform(quad -> {
					quad.fromVanilla(bakedQuad, mesh.mat.getMaterial(RendererAccess.INSTANCE.getRenderer()), bakedQuad.getDirection());
					return true;
				});
			});
		});
		context.popTransform();
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		meshes.forEach(mesh -> {
			mesh.quads.forEach(bakedQuad -> {
				context.pushTransform(quad -> {
					quad.fromVanilla(bakedQuad, mesh.mat.getMaterial(RendererAccess.INSTANCE.getRenderer()), bakedQuad.getDirection());
					return true;
				});
			});
		});
		context.popTransform();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
		List<BakedQuad> bakedQuads = new ArrayList<>();
		meshes.forEach(mesh -> bakedQuads.addAll(mesh.quads()));
		return bakedQuads;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()).sprite();
	}

	@Override
	public ItemTransforms getTransforms() {
		return ItemTransforms.NO_TRANSFORMS;
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	public record Mesh(net.minecraft.resources.ResourceLocation texturePath, List<BakedQuad> quads, ObjMaterialLibrary.Material mat) {
	}
}
