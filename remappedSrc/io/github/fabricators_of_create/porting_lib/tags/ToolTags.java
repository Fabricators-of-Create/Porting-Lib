package io.github.fabricators_of_create.porting_lib.tags;

import net.minecraft.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ToolTags {
	public static final TagKey<Item> PICKAXES = TagKey.create(Registry.ITEM_KEY, new Identifier("c", "pickaxes"));
	public static final TagKey<Item> SHEARS = TagKey.create(Registry.ITEM_KEY, new Identifier("c", "shears"));
	public static final TagKey<Item> SHOVELS = TagKey.create(Registry.ITEM_KEY, new Identifier("c", "shovels"));
	public static final TagKey<Item> AXES = TagKey.create(Registry.ITEM_KEY, new Identifier("c", "axes"));
	public static final TagKey<Item> HOES = TagKey.create(Registry.ITEM_KEY, new Identifier("c", "hoes"));

}
