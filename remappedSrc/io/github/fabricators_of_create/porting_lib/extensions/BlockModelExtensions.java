package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.model.BlockModelConfiguration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface BlockModelExtensions {
  default BlockModelConfiguration getGeometry() {
	  throw new RuntimeException("this should be overridden via mixin. what?");
  }

	default ModelOverrideList getOverrides(ModelLoader pModelBakery, JsonUnbakedModel pModel, Function<SpriteIdentifier, Sprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
