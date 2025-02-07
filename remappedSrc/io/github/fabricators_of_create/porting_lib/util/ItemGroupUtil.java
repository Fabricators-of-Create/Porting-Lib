package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemGroupUtil {
	public static final Identifier ERROR_ID = new Identifier("if_you_see_this", "something_went_wrong");

	public static int expandArrayAndGetId() {
		return FabricItemGroupBuilder.build(ERROR_ID, Items.AIR::getDefaultStack).getIndex();
	}
}
