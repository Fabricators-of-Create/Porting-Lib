package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import com.mojang.serialization.Codec;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AreaSelection {
	public static Codec<AreaSelection> CODEC = RecordCodecBuilder.create(i -> i.group(
			BlockPos.CODEC.fieldOf("first_pos").forGetter(a -> a.first),
			BlockPos.CODEC.fieldOf("second_pos").forGetter(a -> a.second)
	).apply(i, AreaSelection::new));

	public static StreamCodec<ByteBuf, AreaSelection> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, a -> a.first,
			BlockPos.STREAM_CODEC, a -> a.second,
			AreaSelection::new
	);

	public BlockPos first, second;

	public AreaSelection(BlockPos first, BlockPos second) {
		this.first = first;
		this.second = second;
	}

	public BoundingBox asBoundingBox() {
		return BoundingBox.fromCorners(first, second);
	}
}
