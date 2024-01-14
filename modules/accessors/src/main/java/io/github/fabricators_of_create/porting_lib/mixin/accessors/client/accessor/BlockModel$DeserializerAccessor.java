package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.renderer.block.model.BlockModel;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModel.Deserializer.class)
public interface BlockModel$DeserializerAccessor {
	@Invoker("parseTextureLocationOrReference")
	static Either<Material, String> port_lib$parseTextureLocationOrReference(ResourceLocation location, String name) {
		throw new RuntimeException("mixin failed!");
	}
}
