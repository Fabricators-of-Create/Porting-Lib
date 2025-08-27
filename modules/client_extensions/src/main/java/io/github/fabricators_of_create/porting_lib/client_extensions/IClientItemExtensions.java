package io.github.fabricators_of_create.porting_lib.client_extensions;

import io.github.fabricators_of_create.porting_lib.client_extensions.mixin.ItemRendererAccessor;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * {@linkplain EnvType#CLIENT Client-only} extensions to {@link Item}.
 */
public interface IClientItemExtensions {
	IClientItemExtensions DEFAULT = new IClientItemExtensions() {};

	static IClientItemExtensions of(ItemStack stack) {
		return of(stack.getItem());
	}

	static IClientItemExtensions of(Item item) {
		return ClientExtensionsRegistry.ITEM_EXTENSIONS.getOrDefault(item, DEFAULT);
	}

	static boolean exists(ItemStack stack) {
		return exists(stack.getItem());
	}

	static boolean exists(Item item) {
		return ClientExtensionsRegistry.ITEM_EXTENSIONS.containsKey(item);
	}

	default BlockEntityWithoutLevelRenderer getCustomRenderer() {
		return ((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer();
	}
}
