package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;

@Environment(EnvType.CLIENT)
@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
	@Accessor("height")
	void port_lib$setHeight(int height);
}
