package io.github.fabricators_of_create.porting_lib.util;

import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.util.Identifier;

public class ArmorTextureRegistry {
	private static final Map<ArmorMaterial, Identifier> TEXTURES = new IdentityHashMap<>();

	public static void register(ArmorMaterial material, Identifier location) {
		TEXTURES.put(material, location);
	}

	@Nullable
	public static Identifier get(ArmorMaterial material) {
		return TEXTURES.get(material);
	}
}
