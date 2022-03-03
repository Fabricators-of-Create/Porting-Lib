package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityHelper {
	public static final String EXTRA_DATA_KEY = "ForgeData";

	public static CompoundTag getExtraCustomData(BlockEntity be) {
		return be.getExtraCustomData();
	}
}
