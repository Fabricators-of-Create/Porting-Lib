package io.github.fabricators_of_create.porting_lib.loot.mixin;

import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.loot.LootHooks;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@WrapOperation(
			method = "dropFromLootTable",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"
			)
	)
	private void wrapConsumer(LootTable lootTable, LootContext ctx, Consumer<ItemStack> consumer, Operation<Void> original) {
		ObjectArrayList<ItemStack> stacks = new ObjectArrayList<>();
		Consumer<ItemStack> collector = stacks::add;
		original.call(lootTable, ctx, collector);
		ObjectArrayList<ItemStack> modified = LootHooks.modifyLoot(lootTable.getLootTableId(), stacks, ctx);
		modified.forEach(consumer);
	}
}
