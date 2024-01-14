package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("children")
	List<GuiEventListener> port_lib$getChildren();

	@Accessor("minecraft")
	Minecraft port_lib$getMinecraft();

	@Accessor("renderables")
	List<Renderable> port_lib$getRenderables();
}
