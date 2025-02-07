package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import java.util.Map;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;

@Mixin(JsonUnbakedModel.class)
public interface BlockModelAccessor {
	@Accessor("GSON")
	static Gson port_lib$GSON() {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor()
	@Mutable
	static void setGSON(Gson newGson) {
		throw new RuntimeException("mixin failed!");
	}

	@Invoker("bakeFace")
	static BakedQuad port_lib$bakeFace(ModelElement part, ModelElementFace partFace, Sprite sprite, Direction direction, ModelBakeSettings transform, Identifier location) {
		throw new RuntimeException("mixin failed!");
	}

	@Accessor("textureMap")
	Map<String, Either<SpriteIdentifier, String>> port_lib$textureMap();
}
