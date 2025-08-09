package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.kinds.App;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.core.util.PortingLibExtraCodecs;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import io.github.fabricators_of_create.porting_lib.entity.injects.MobEffectInstance$DetailsInjection;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(MobEffectInstance.Details.class)
public class MobEffectInstance$DetailsMixin implements MobEffectInstance$DetailsInjection {
	@Unique
	private Optional<Set<EffectCure>> port_lib$cures = Optional.empty();

	@Override
	public Optional<Set<EffectCure>> port_lib$getCures() {
		return port_lib$cures;
	}

	@Override
	public void port_lib$setCures(Optional<Set<EffectCure>> cures) {
		port_lib$cures = cures;
	}

	@ModifyReturnValue(method = "method_56672", at = @At("RETURN"))
	private static App<RecordCodecBuilder.Mu<MobEffectInstance.Details>, MobEffectInstance.Details> appendEffectCureCodec(App<RecordCodecBuilder.Mu<MobEffectInstance.Details>, MobEffectInstance.Details> original, @Local(argsOnly = true) RecordCodecBuilder.Instance<MobEffectInstance.Details> instance) {
		return instance.group(
				original,
				PortingLibExtraCodecs.setOf(EffectCure.CODEC)
						.optionalFieldOf("porting_lib:cures")
						.forGetter(details -> ((MobEffectInstance$DetailsInjection) (Object) details).port_lib$getCures())
		)
				.apply(instance, (details, cures) -> {
					((MobEffectInstance$DetailsInjection) (Object) details).port_lib$setCures(cures);
					return details;
				});
	}

	@ModifyReturnValue(method = "method_57279", at = @At("RETURN"))
	private static StreamCodec<ByteBuf, MobEffectInstance.Details> appendEffectCureStreamCodec(StreamCodec<ByteBuf, MobEffectInstance.Details> original) {
		return StreamCodec.composite(
				original,
				d -> d,
				ByteBufCodecs.optional(EffectCure.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new))),
				d -> ((MobEffectInstance$DetailsInjection) (Object) d).port_lib$getCures(),
				(details, cures) -> {
					((MobEffectInstance$DetailsInjection) (Object) details).port_lib$setCures(cures);
					return details;
				}
		);
	}
}
