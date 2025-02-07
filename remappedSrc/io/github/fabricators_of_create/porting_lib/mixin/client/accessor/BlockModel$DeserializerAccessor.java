package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(JsonUnbakedModel.Deserializer.class)
public interface BlockModel$DeserializerAccessor {
	@Invoker("parseTextureLocationOrReference")
	static Either<SpriteIdentifier, String> port_lib$parseTextureLocationOrReference(Identifier location, String name) {
		throw new RuntimeException("mixin failed!");
	}
}
