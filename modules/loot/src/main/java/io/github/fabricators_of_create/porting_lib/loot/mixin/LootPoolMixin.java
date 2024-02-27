package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolBuilderExtension;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolExtensions;
import net.minecraft.world.level.storage.loot.LootPool;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolExtensions {
	@Unique
	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@ModifyExpressionValue(
			method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;")
	)
	private static Codec<LootPool> modifyCodec(Codec<LootPool> original) {
		// TODO: test this
		return Codec.pair(original, Codec.STRING.optionalFieldOf("name").codec()).xmap(pair -> {
			LootPool pool = pair.getFirst();
			pool.setName(pair.getSecond().filter(name -> !name.startsWith("custom#")).orElse(null));
			return pool;
		}, pool -> {
			String name = pool.getName();
			return Pair.of(pool, Optional.ofNullable(name));
		});
	}

	@Mixin(LootPool.Builder.class)
	public static class LootPoolBuilderMixin implements LootPoolBuilderExtension {
		@Unique
		private String name;

		@Override
		public LootPool.Builder name(String name) {
			this.name = name;
			//noinspection DataFlowIssue
			return (LootPool.Builder) (Object) this;
		}

		@ModifyReturnValue(method = "build", at = @At("RETURN"))
		public LootPool setName(LootPool pool) {
			pool.setName(name);
			return pool;
		}
	}
}
