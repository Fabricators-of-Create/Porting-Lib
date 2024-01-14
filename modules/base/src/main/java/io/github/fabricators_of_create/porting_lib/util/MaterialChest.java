package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

public interface MaterialChest<T extends BlockEntity & LidBlockEntity> {
	 Material getMaterial(T blockEntity, ChestType chestType);
}
