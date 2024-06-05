package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AreaSelection {
	public BlockPos first, second;

	public AreaSelection(BlockPos first, BlockPos second) {
		this.first = first;
		this.second = second;
	}

	public BoundingBox asBoundingBox() {
		return BoundingBox.fromCorners(first, second);
	}

	public CompoundTag toNbt() {
		CompoundTag data = new CompoundTag();
		if (first != null)
			data.put("first", NbtUtils.writeBlockPos(first));
		if (second != null)
			data.put("second", NbtUtils.writeBlockPos(second));
		return data;
	}

	public static AreaSelection fromNbt(CompoundTag tag) {
		BlockPos first = tag.contains("first", Tag.TAG_COMPOUND) ? NbtUtils.readBlockPos(tag, "first").orElse(null) : null;
		BlockPos second = tag.contains("second", Tag.TAG_COMPOUND) ? NbtUtils.readBlockPos(tag, "second").orElse(null) : null;
		return new AreaSelection(first, second);
	}
}
