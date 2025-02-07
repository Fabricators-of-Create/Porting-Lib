package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.util.SpriteIdentifier;

public interface MaterialChest {
	SpriteIdentifier getMaterial(BlockEntity blockEntity, ChestType chestType);
}
