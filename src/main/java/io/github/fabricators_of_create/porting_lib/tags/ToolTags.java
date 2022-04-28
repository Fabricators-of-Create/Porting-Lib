package io.github.fabricators_of_create.porting_lib.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ToolTags {
	public static final TagKey<Item> PICKAXES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "pickaxes"));
	public static final TagKey<Item> SHEARS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "shears"));
	public static final TagKey<Item> SHOVELS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "shovels"));
	public static final TagKey<Item> AXES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "axes"));
	public static final TagKey<Item> HOES = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", "hoes"));

}
