package io.github.fabricators_of_create.porting_lib.blocks.client.extensions;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;

import org.jspecify.annotations.Nullable;

/**
 * Implement on custom instances of {@link net.minecraft.client.renderer.blockentity.ChestRenderer} to override there material
 */
public interface MaterialChest<T extends BlockEntity & LidBlockEntity> {
	RenderStateDataKey<Material> KEY = RenderStateDataKey.create(() -> "custom_material");

	/**
	 * Neo: Return a custom {@link Material} to render the chest with or {@code null} to
	 * fall back to the vanilla material selection.
	 */
	@Nullable
	default Material getCustomMaterial(T blockEntity, ChestRenderState renderState) {
		return null;
	}
}
