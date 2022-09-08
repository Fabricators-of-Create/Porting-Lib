package io.github.fabricators_of_create.porting_lib.tags;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * @deprecated use {@link ConventionalItemTags}
 */
@Deprecated
public class ToolTags {
	public static final TagKey<Item> PICKAXES = ConventionalItemTags.PICKAXES;
	public static final TagKey<Item> SHEARS = ConventionalItemTags.SHEARS;
	public static final TagKey<Item> SHOVELS = ConventionalItemTags.SHOVELS;
	public static final TagKey<Item> AXES = ConventionalItemTags.AXES;
	public static final TagKey<Item> HOES = ConventionalItemTags.HOES;

}
