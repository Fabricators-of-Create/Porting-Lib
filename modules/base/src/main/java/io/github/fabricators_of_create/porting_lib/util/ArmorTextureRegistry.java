package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;

import javax.annotation.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A Map of ArmorMaterials to ResourceLocations for armor textures.
 */
public class ArmorTextureRegistry {
	private static final Map<ArmorMaterial, ResourceLocation> TEXTURES = new IdentityHashMap<>();

	public static void register(ArmorMaterial material, ResourceLocation location) {
		TEXTURES.put(material, location);
	}

	@Nullable
	public static ResourceLocation get(ArmorMaterial material) {
		return TEXTURES.get(material);
	}
}
