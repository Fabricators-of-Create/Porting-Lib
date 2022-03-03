package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.extensions.BlockModelExtensions;
import io.github.fabricators_of_create.porting_lib.model.BlockModelConfiguration;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.block.model.BlockModel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin implements BlockModelExtensions {
	@Shadow
	@Final
	private List<ItemOverride> overrides;

	@Shadow
	public abstract BlockModel getRootModel();

	@Shadow
	public abstract Material getMaterial(String name);

	@Shadow
	public abstract List<BlockElement> getElements();

	@Shadow
	public String name;
	@Shadow
	@Nullable
	public ResourceLocation parentLocation;
	@Shadow
	@Final
	private static Logger LOGGER;
	@Unique
  private BlockModelConfiguration data = new BlockModelConfiguration((BlockModel) (Object) this);

  @Override
  public BlockModelConfiguration getGeometry() {
    return data;
  }

	  /**
	   * @author AlphaMode
	   * TODO: Replace with ASM PortingLibER
	   */
  	@Overwrite
	public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> pModelGetter, Set<Pair<String, String>> pMissingTextureErrors) {
		Set<UnbakedModel> set = Sets.newLinkedHashSet();
		for(BlockModel blockmodel = (BlockModel) (Object) this; blockmodel.parentLocation != null && blockmodel.parent == null; blockmodel = blockmodel.parent) {
			set.add(blockmodel);
			UnbakedModel unbakedmodel = pModelGetter.apply(blockmodel.parentLocation);
			if (unbakedmodel == null) {
				LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, blockmodel);
			}

			if (set.contains(unbakedmodel)) {
				LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", blockmodel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
				unbakedmodel = null;
			}

			if (unbakedmodel == null) {
				blockmodel.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
				unbakedmodel = pModelGetter.apply(blockmodel.parentLocation);
			}

			if (!(unbakedmodel instanceof BlockModel)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			blockmodel.parent = (BlockModel)unbakedmodel;
		}

		Set<Material> set1 = Sets.newHashSet(this.getMaterial("particle"));

		if (data.hasCustomGeometry())
			set1.addAll(data.getTextureDependencies(pModelGetter, pMissingTextureErrors));
		else
			for(BlockElement blockelement : this.getElements()) {
				for(BlockElementFace blockelementface : blockelement.faces.values()) {
					Material material = this.getMaterial(blockelementface.texture);
					if (Objects.equals(material.texture(), MissingTextureAtlasSprite.getLocation())) {
						pMissingTextureErrors.add(Pair.of(blockelementface.texture, this.name));
					}

					set1.add(material);
				}
			}

		this.overrides.forEach((p_111475_) -> {
			UnbakedModel unbakedmodel1 = pModelGetter.apply(p_111475_.getModel());
			if (!Objects.equals(unbakedmodel1, this)) {
				set1.addAll(unbakedmodel1.getMaterials(pModelGetter, pMissingTextureErrors));
			}
		});
		if (this.getRootModel() == ModelBakery.GENERATION_MARKER) {
			ItemModelGenerator.LAYERS.forEach((p_111467_) -> {
				set1.add(this.getMaterial(p_111467_));
			});
		}

		return set1;
	}
}
