package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

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
			Identifier id = Registry.FLUID.getId(fluid);
			String key = Util.createTranslationKey("block", id);
			String translated = I18n.translate(key);
			translationKey = translated.equals(key) ? Util.createTranslationKey("fluid", id) : key;
		}

		return translationKey;
	}
}
