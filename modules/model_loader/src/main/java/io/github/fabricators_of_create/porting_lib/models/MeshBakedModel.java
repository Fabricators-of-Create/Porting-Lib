package io.github.fabricators_of_create.porting_lib.models;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Based off of {@link net.minecraft.client.resources.model.SimpleBakedModel}
 */
public class MeshBakedModel implements BakedModel {
	protected final Mesh mesh;
	protected final boolean hasAmbientOcclusion;
	protected final boolean isGui3d;
	protected final boolean usesBlockLight;
	protected final TextureAtlasSprite particleIcon;
	protected final ItemTransforms transforms;
	protected final ItemOverrides overrides;

	public MeshBakedModel(Mesh mesh, boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides) {
		this.mesh = mesh;
		this.hasAmbientOcclusion = hasAmbientOcclusion;
		this.isGui3d = isGui3d;
		this.usesBlockLight = usesBlockLight;
		this.particleIcon = particleIcon;
		this.transforms = transforms;
		this.overrides = overrides;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
		return ModelHelper.toQuadLists(mesh)[ModelHelper.toFaceIndex(direction)];
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		mesh.outputTo(context.getEmitter());
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		mesh.outputTo(context.getEmitter());
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.hasAmbientOcclusion;
	}

	@Override
	public boolean isGui3d() {
		return this.isGui3d;
	}

	@Override
	public boolean usesBlockLight() {
		return this.usesBlockLight;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.particleIcon;
	}

	@Override
	public ItemTransforms getTransforms() {
		return this.transforms;
	}

	@Override
	public ItemOverrides getOverrides() {
		return this.overrides;
	}
}
