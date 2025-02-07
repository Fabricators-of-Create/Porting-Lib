package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("children")
	List<Element> port_lib$getChildren();

	@Accessor("minecraft")
	MinecraftClient port_lib$getMinecraft();

	@Accessor("renderables")
	List<Drawable> port_lib$getRenderables();
}
