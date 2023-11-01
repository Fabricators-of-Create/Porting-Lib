package io.github.fabricators_of_create.porting_lib.loot.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolBuilderExtension;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolExtensions;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolExtensions {
	@Mutable
	@Shadow
	@Final
	public static Codec<LootPool> CODEC;
	@Shadow
	@Final
	public List<LootItemCondition> conditions;
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
		public void setName(LootPool pool) {
			pool.setName(name);
		}
	}

	// TODO: more sane compatible solution?
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void addName(CallbackInfo ci) {
		CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter((lootPool) -> {
				return lootPool.entries;
			}), ExtraCodecs.strictOptionalField(LootItemConditions.CODEC.listOf(), "conditions", List.of()).forGetter((lootPool) -> {
				return lootPool.conditions;
			}), ExtraCodecs.strictOptionalField(LootItemFunctions.CODEC.listOf(), "functions", List.of()).forGetter((lootPool) -> {
				return lootPool.functions;
			}), NumberProviders.CODEC.fieldOf("rolls").forGetter((lootPool) -> {
				return lootPool.rolls;
			}), NumberProviders.CODEC.fieldOf("bonus_rolls").orElse(ConstantValue.exactly(0.0F)).forGetter((lootPool) -> {
				return lootPool.bonusRolls;
			}), Codec.STRING.optionalFieldOf("name").forGetter(lootPool -> {
				String name = lootPool.getName();
				if (name != null && !name.startsWith("custom#"))
					return Optional.of(name);
				return Optional.empty();
			})).apply(instance, (entries, conditions, functions, rolls, bonus_rolls, name) -> {
				LootPool pool = LootPoolAccessor.createLootPool(entries, conditions, functions, rolls, bonus_rolls);
				pool.setName(name.orElse(null));
				return pool;
			});
		});
	}
}
