package io.github.fabricators_of_create.porting_lib.models.builders;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record MaterialData(int color, boolean emissive, boolean ambientOcclusion, ResourceLocation blendMode) {
	public static final MaterialData DEFAULT = new MaterialData(0xFFFFFFFF, false, true, new ResourceLocation("solid"));

	public static final Codec<Integer> COLOR = new ExtraCodecs.EitherCodec<>(Codec.INT, Codec.STRING).xmap(
			either -> either.map(Function.identity(), str -> (int) Long.parseLong(str, 16)),
			color -> Either.right(Integer.toHexString(color)));

	public static final Codec<MaterialData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
					COLOR.optionalFieldOf("color", 0xFFFFFFFF).forGetter(MaterialData::color),
					Codec.BOOL.optionalFieldOf("emissive", false).forGetter(MaterialData::emissive),
					Codec.BOOL.optionalFieldOf("ambient_occlusion", true).forGetter(MaterialData::ambientOcclusion),
					ResourceLocation.CODEC.optionalFieldOf("blendMode", new ResourceLocation("solid")).forGetter(MaterialData::blendMode))
			.apply(builder, MaterialData::new));
}
