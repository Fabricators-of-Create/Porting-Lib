package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
@Mixin(Font.class)
public interface FontAccessor {
	@Invoker("getFontSet")
	FontSet port_lib$getFontSet(ResourceLocation resourceLocation);
}
