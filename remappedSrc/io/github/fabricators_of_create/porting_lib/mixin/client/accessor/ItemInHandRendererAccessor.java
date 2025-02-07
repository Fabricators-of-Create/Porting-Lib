package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public interface ItemInHandRendererAccessor {
	@Accessor("mainHandItem")
	ItemStack port_lib$getMainHandItem();

	@Accessor("offHandItem")
	ItemStack port_lib$getOffHandItem();
}
