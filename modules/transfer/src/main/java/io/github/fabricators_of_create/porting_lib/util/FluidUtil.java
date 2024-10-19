package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

public class FluidUtil {
	@Environment(EnvType.CLIENT)
	public static String getTranslationKey(Fluid fluid) {
		String translationKey;

		if (fluid == Fluids.EMPTY) {
			translationKey = "";
		} else if (fluid == Fluids.WATER) {
			translationKey = "block.minecraft.water";
		} else if (fluid == Fluids.LAVA) {
			translationKey = "block.minecraft.lava";
		} else {
			ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
			String key = Util.makeDescriptionId("block", id);
			String translated = I18n.get(key);
			translationKey = translated.equals(key) ? Util.makeDescriptionId("fluid", id) : key;
		}

		return translationKey;
	}

	/**
	 * Helper method to get the fluid contained in an itemStack
	 */
	public static Optional<FluidStack> getFluidContained(ItemStack container) {
		if (!container.isEmpty()) {
			container = container.copyWithCount(1);
			Optional<FluidStack> fluidContained = Optional.ofNullable(ContainerItemContext.withConstant(container).find(FluidStorage.ITEM))
					.map(handler -> StorageUtil.findExtractableContent(handler, null))
					.map(FluidStack::new);

			if (fluidContained.isPresent() && !fluidContained.get().isEmpty()) {
				return fluidContained;
			}
		}
		return Optional.empty();
	}
}
