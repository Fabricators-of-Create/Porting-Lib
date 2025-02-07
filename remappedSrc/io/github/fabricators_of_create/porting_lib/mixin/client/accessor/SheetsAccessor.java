package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TexturedRenderLayers.class)
public interface SheetsAccessor {
	@Invoker("createSignMaterial")
	static SpriteIdentifier port_lib$createSignMaterial(SignType woodType) {
		throw new RuntimeException("mixin failed!");
	}
}
