package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int port_lib$getGuiLeft();

	@Accessor("topPos")
	int port_lib$getGuiTop();
}
