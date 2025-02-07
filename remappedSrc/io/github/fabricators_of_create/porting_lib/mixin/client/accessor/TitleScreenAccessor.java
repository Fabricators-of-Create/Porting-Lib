package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;

@Environment(EnvType.CLIENT)
@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {
	@Accessor("panorama")
	RotatingCubeMapRenderer port_lib$getPanorama();
}
