package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

@Environment(EnvType.CLIENT)
@Mixin(BlockRenderManager.class)
public interface BlockRenderDispatcherAccessor {
	@Accessor("blockEntityRenderer")
	BuiltinModelItemRenderer getBlockEntityRenderer();
}
