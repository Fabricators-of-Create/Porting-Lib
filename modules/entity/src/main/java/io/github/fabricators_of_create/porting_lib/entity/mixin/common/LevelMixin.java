package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.entity.ext.LevelExt;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Level.class, priority = 1100) // need to apply after lithium
public class LevelMixin implements LevelExt {
	@Unique
	final Int2ObjectMap<PartEntity<?>> port_lib$multiparts = new Int2ObjectOpenHashMap<>();

	@Inject(
			method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
			at = @At("TAIL"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void appendPartEntitiesPredicate(@Nullable Entity entity, AABB area, Predicate<? super Entity> predicate, CallbackInfoReturnable<List<Entity>> cir, List<Entity> list) {
		for (PartEntity<?> p : this.getPartEntities()) {
			if (p != entity && p.getBoundingBox().intersects(area) && predicate.test(p)) {
				list.add(p);
			}
		}
	}

	@Inject(
			method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
			at = @At("TAIL")
	)
	private <T extends Entity> void appendPartEntitiesTypeTest(EntityTypeTest<Entity, T> test, AABB area, Predicate<? super T> predicate, CallbackInfoReturnable<List<T>> cir, @Local List<Entity> list) {
		for (PartEntity<?> p : this.getPartEntities()) {
			T t = test.tryCast(p);
			if (t != null && t.getBoundingBox().intersects(area) && predicate.test(t)) {
				list.add(t);
			}
		}
	}

	@Override
	public Int2ObjectMap<PartEntity<?>> getPartEntityMap() {
		return port_lib$multiparts;
	}
}
