package io.github.fabricators_of_create.porting_lib.client.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ArmorRendererRegistry {
	private static final HashMap<Item, ArmorRenderer> RENDERERS = new HashMap<>();

	public static void register(ArmorRenderer renderer, ItemLike... items) {
		Objects.requireNonNull(renderer, "renderer is null");

		if (items.length == 0) {
			throw new IllegalArgumentException("Armor renderer registered for no item");
		}

		for (ItemLike item : items) {
			Objects.requireNonNull(item.asItem(), "armor item is null");

			if (RENDERERS.putIfAbsent(item.asItem(), renderer) != null) {
				throw new IllegalArgumentException("Custom armor renderer already exists for " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Nullable
	public static ArmorRenderer get(Item item) {
		return RENDERERS.get(item);
	}
}
