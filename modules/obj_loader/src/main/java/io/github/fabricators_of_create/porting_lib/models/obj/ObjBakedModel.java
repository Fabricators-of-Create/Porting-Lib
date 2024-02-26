package io.github.fabricators_of_create.porting_lib.models.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ObjBakedModel implements BakedModel {

	private final List<Mesh> meshes;
	private final boolean ao, isGui3d, blockLight, customRenderer;
	private final ItemTransforms transforms;
	private final ItemOverrides overrides;
	private final TextureAtlasSprite particle;

	public ObjBakedModel(List<Mesh> meshes, boolean ao, boolean isGui3d, boolean blockLight, boolean customRenderer,
						 ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle) {
		this.meshes = meshes;
		this.ao = ao;
		this.isGui3d = isGui3d;
		this.blockLight = blockLight;
		this.customRenderer = customRenderer;
		this.transforms = transforms;
		this.overrides = overrides;
		this.particle = particle;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		meshes.forEach(mesh -> mesh.outputTo(context.getEmitter()));
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
		meshes.forEach(mesh -> mesh.outputTo(context.getEmitter()));
	}

	@Override
	@NotNull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, @NotNull RandomSource random) {
		AbstractTexture atlasTexture = Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS);
		if (atlasTexture instanceof TextureAtlas atlas) {
			ArrayList<BakedQuad> quads = new ArrayList<>();
			SpriteFinder finder = SpriteFinder.get(atlas);
			meshes.forEach(mesh -> mesh.forEach(quad -> {
				TextureAtlasSprite sprite = finder.find(quad);
				quads.add(quad.toBakedQuad(sprite));
			}));
			return quads;
		}
		return List.of();
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return ao;
	}

	@Override
	public boolean isGui3d() {
		return isGui3d;
	}

	@Override
	public boolean usesBlockLight() {
		return blockLight;
	}

	@Override
	public boolean isCustomRenderer() {
		return customRenderer;
	}

	@Override
	@NotNull
	public TextureAtlasSprite getParticleIcon() {
		return particle;
	}

	@Override
	@NotNull
	public ItemTransforms getTransforms() {
		return transforms;
	}

	@Override
	@NotNull
	public ItemOverrides getOverrides() {
		return overrides;
	}
}
