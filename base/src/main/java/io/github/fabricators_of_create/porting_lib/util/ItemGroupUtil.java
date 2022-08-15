package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ItemGroupUtil {
	public static final ResourceLocation ERROR_ID = new ResourceLocation("if_you_see_this", "something_went_wrong");

	public static int expandArrayAndGetId() {
		return FabricItemGroupBuilder.build(ERROR_ID, Items.AIR::getDefaultInstance).getId();
	}
}
