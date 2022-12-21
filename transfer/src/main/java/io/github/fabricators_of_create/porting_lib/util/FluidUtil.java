package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

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
}
