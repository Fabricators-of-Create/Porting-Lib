package io.github.fabricators_of_create.porting_lib.armor.api.client;

import com.google.common.collect.Maps;

import io.github.fabricators_of_create.porting_lib.armor.api.client.addons.ArmorTextureItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

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

	public static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

	public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
		String result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, type);
		return result != null ? result : _default;
	}

	/**
	 * More generic version of the above function, it allows for Items to have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack ItemStack for the armor
	 * @param slot Slot ID that the item is in
	 * @param type Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	public static ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
		ArmorItem item = (ArmorItem)stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

		s1 = getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}

		return resourcelocation;
	}
}
