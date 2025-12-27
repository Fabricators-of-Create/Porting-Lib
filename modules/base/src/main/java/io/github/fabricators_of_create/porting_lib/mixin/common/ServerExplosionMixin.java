package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.fabricators_of_create.porting_lib.event.common.ExplosionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
	@Shadow
	@Final
	private ServerLevel level;

	@Unique private List<BlockPos> blocks = List.of();

	@WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;hurtEntities()V"))
	private void storeExplodedBlockPositions(ServerExplosion instance, Operation<Void> original, @Local LocalRef<List<BlockPos>> blocksRef) {
		blocks = new ArrayList<>(blocksRef.get());
		original.call(instance);
		blocksRef.set(blocks);
		blocks = List.of();
	}

	@WrapOperation(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	public List<Entity> onExplode(ServerLevel instance, Entity entity, AABB aabb, Operation<List<Entity>> original) {
		List<Entity> list = new ArrayList<>(original.call(instance, entity, aabb));
		ExplosionEvents.DETONATE.invoker().onDetonate(this.level, (ServerExplosion) (Object) this, list, blocks);
		return list;
	}

	@ModifyExpressionValue(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
	private Vec3 onExplosionKnockback(Vec3 original, @Local Entity entity) {
		return ExplosionEvents.KNOCKBACK.invoker().updateExplosionKnockback(this.level, (ServerExplosion) (Object) this, entity, original, blocks);
	}
}
