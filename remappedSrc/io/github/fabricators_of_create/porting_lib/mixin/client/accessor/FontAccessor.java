package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(TextRenderer.class)
public interface FontAccessor {
	@Invoker("getFontSet")
	FontStorage port_lib$getFontSet(Identifier resourceLocation);
}
